package com.qian.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qian.usercenter.comment.*;
import com.qian.usercenter.job.exception.BusinessException;
import com.qian.usercenter.model.domain.Team;
import com.qian.usercenter.mapper.TeamMapper;
import com.qian.usercenter.model.domain.User;
import com.qian.usercenter.model.domain.UserTeam;
import com.qian.usercenter.model.dto.*;
import com.qian.usercenter.model.vo.TeamVO;
import com.qian.usercenter.model.vo.UserVO;
import com.qian.usercenter.service.TeamService;
import com.qian.usercenter.service.UserService;
import com.qian.usercenter.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


/**
* @author Yu
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-03-29 16:36:22
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;
    /**
     * 创建队伍
     * @param createTeamRequest
     * @param userLogin
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public long createTeams(Team createTeamRequest, User userLogin) {
        if ( createTeamRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (userLogin == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer num = Optional.ofNullable( createTeamRequest.getMaxNum()).orElse(0);
        if (num < 1 || num > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不符合规范");
        }
        String name =  createTeamRequest.getName();
        if (StringUtils.isBlank(name) || name.length() > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍名称不符合规范");
        }
        String description =  createTeamRequest.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512 ) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述不符合规范");
        }
        Integer integer = Optional.ofNullable( createTeamRequest.getStatus()).orElse(0);
        TeamStatusEnum teamStatus = TeamStatusEnum.getTeamStatus(integer);
        if (teamStatus == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不符合要求");
        }
        String password =  createTeamRequest.getPassword();
        if (TeamStatusEnum.SECRECT.equals(teamStatus)) {
            if (StringUtils.isBlank(password) || password.length()>32 ) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不符合规范");
            }
        }
       Date expireTime =  createTeamRequest.getExpireTime();
        if (new Date().after(expireTime)||expireTime == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"日期错误");
        }
        final long userId = userLogin.getId();
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("userId",userId);
        long count = this.count(teamQueryWrapper);
        if (count >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍超过5个");
        }
        Team team = new Team();
        BeanUtils.copyProperties(createTeamRequest,team);
        team.setId(null);
        team.setUserId(userId);
        boolean save = this.save(team);
        if (!save) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(team.getId());
        userTeam.setUserId(userId);
        userTeam.setCreateTime(new Date());
        userTeamService.save(userTeam);
        return team.getId();
    }

    /**
     * 查询队伍信息
     * @param requestQuery
     * @param isAdmin
     * @return
     */
    @Override
    public List<TeamVO> search(RequestQuery requestQuery,boolean isAdmin) {
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        if (requestQuery != null) {
           String name = requestQuery.getName();
           String description = requestQuery.getDescription();
           Integer maxNum = requestQuery.getMaxNum();
           Long userId = requestQuery.getUserId();
           Integer status = requestQuery.getStatus();
           //判断名字是否为空
           if (StringUtils.isNotBlank(name)) {
               teamQueryWrapper.eq("name",name);
           }
           //判断描述是否为空
           if (StringUtils.isNotBlank(description)) {
               teamQueryWrapper.eq("description",description);
           }
           //判断最大人数是否为空
           if (maxNum != null && maxNum >0) {
               teamQueryWrapper.eq("maxNum",maxNum);
           }
           //判断用户id是否为空
           if (userId != null && userId >0) {
               teamQueryWrapper.eq("userId",userId);
           }
           //判断用户状态是否合规
           TeamStatusEnum teamStatus = TeamStatusEnum.getTeamStatus(status);
           if (teamStatus == null ) {
               teamStatus = TeamStatusEnum.PUBLIC;
           }
           if (!isAdmin && !teamStatus.equals(TeamStatusEnum.PUBLIC)) {
               throw new BusinessException(ErrorCode.NO_AUTH);
           }
           teamQueryWrapper.eq("status",teamStatus.getValue());
       }
        teamQueryWrapper.and(qw->qw.gt("expireTime",new Date()).or().isNull("expireTime"));
        List<Team> list = this.list(teamQueryWrapper);
        this.list(teamQueryWrapper);
        if (CollectionUtils.isEmpty(list)) {
         return new ArrayList<>();
        }
        List<TeamVO> teamVOS = new ArrayList<>();
        for (Team team:list
             ) {
            //建立队伍脱敏对象
            TeamVO teamVO = new TeamVO();
            //获取队伍中的用户id
            Long userteamById = teamVO.getUserId();
            if (userteamById == null) {
                continue;
            }
            //将Team对象转换为TeamVO对象
            BeanUtils.copyProperties(team,teamVO);
            //建立User脱敏对象
            UserVO userVO = new UserVO();
            User user = userService.getById(userteamById);
            if (user != null) {
                //将User类型转换为UserVO类型
                BeanUtils.copyProperties(user,userVO);
                teamVO.setCreateUser(userVO);
            }
            teamVOS.add(teamVO);
        }
        return teamVOS;
    }

    /**
     * 更新队伍信息
     * @param team
     * @param loginUser
     * @return
     */
    @Override
    public boolean updateTeam(RequestUpdate team, User loginUser) {
        //1. 判断请求参数是否为空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2. 查询队伍是否存在
        Long TeamId = team.getId();
        if (TeamId == null || TeamId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team TeamById = this.getById(TeamId);
        if (TeamById == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        //3. 只有管理员或者队伍的创建者可以修改
        Long userId = TeamById.getUserId();
        Long id1 = loginUser.getId();
        boolean admin = Utils.isAdmin(loginUser);
        if (userId != id1 || !admin) {
            throw new BusinessException(ErrorCode.NO_AUTH,"没有权限");
        }
        //4. 如果用户传入的新值和老值一致，就不用 update 了（可自行实现，降低数据库使用次数）TODO
        //5. 如果队伍状态改为加密，必须要有密码
        Integer status = team.getStatus();
        String password = team.getPassword();
        if (status != null) {
            TeamStatusEnum teamStatus = TeamStatusEnum.getTeamStatus(status);
            if (teamStatus.equals(TeamStatusEnum.SECRECT) && password == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密队伍，必须要传密码");
            }
        }
        //6. 更新成功
        Team updateTeam = new Team();
        BeanUtils.copyProperties(team,updateTeam);
        boolean b = this.updateById(updateTeam);
        return b;
    }

    /**
     * 用户加入队伍
     * @param team
     * @param loginUser
     * @return
     */
    @Override
    public boolean joinTeam(RequestJoin team, User loginUser) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //获取要加入队伍id
        Long teamById = team.getId();
        if (teamById == null || teamById <0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入队伍id错误");
        }
        //判断目标队伍是否存在
        Team byId = this.getById(teamById);
        if (byId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        //判断队伍是否过期
        if (byId.getExpireTime().before(new Date())) {
           throw new BusinessException(ErrorCode.SYSTEM_ERROR,"队伍已过期");
        }
        //禁止加入私密队伍
        TeamStatusEnum teamStatus = TeamStatusEnum.getTeamStatus(byId.getStatus());
        if (teamStatus.equals(TeamStatusEnum.PRIVATE)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"禁止加入私密队伍");
        }
        //若队伍为加密状态，则需要输入密码
        if (teamStatus.equals(TeamStatusEnum.SECRECT)) {
            String password = team.getPassword();
            String password1 = byId.getPassword();
            if (password.isEmpty()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不能为空");
            }
            if (password != password1) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码错误");
            }
        }
        //判断队伍人数是否已满
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId",teamById);
        long count1 = userTeamService.count(userTeamQueryWrapper);
        if (count1 == byId.getMaxNum()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数已满");
        }
        //不能加入自己的队伍
        Long id = loginUser.getId();
        if (id == team.getUserId() ) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"不能加入自己队伍");
        }
        //不能重复加入队伍
        userTeamQueryWrapper.eq("userId",id);
        long count2 = userTeamService.count(userTeamQueryWrapper);
        if (count2 >0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"不能重复加入队伍");
        }
        //限制加入队伍数量（最大为5）
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("userId",id);
        long count = this.count(teamQueryWrapper);
        if (count >=5) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"每个人只能对多加入5个队伍");
        }
        //保存信息
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(team.getId());
        userTeam.setUserId(loginUser.getId());
        boolean save = userTeamService.save(userTeam);
        return save;
    }

    /**
     * 用户退出队伍
     * @param requestQuit
     * @param loginUser
     * @return
     */
    @Override
    public boolean quitTeam(RequestQuit requestQuit, User loginUser) {
        //判断参数是否为空
        if (requestQuit == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断队伍是否存在
        Long teamId = requestQuit.getId();
        if (teamId == null || teamId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team byId = this.getById(teamId);
        if (byId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"该队伍不存在");
        }
        //查询是否加入队伍
        Long id = loginUser.getId();
        UserTeam userTeam1 = new UserTeam();
        userTeam1.setUserId(id);
        userTeam1.setTeamId(teamId);
        QueryWrapper<UserTeam> userTeamQueryWrapper2 = new QueryWrapper<>(userTeam1);
        long count1 = userTeamService.count(userTeamQueryWrapper2);
        if (count1 ==0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"未加入该队伍");
        }
        //判断是队伍中的人数，查看队伍中是否只剩一个人，若是，则删除队伍信息
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId",teamId);
        long count = userTeamService.count(userTeamQueryWrapper);
        if (count < 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        if (count == 1) {
            boolean b = this.removeById(teamId);
        }else {
            //若队长退出队伍，移交队长位置
            if (id == byId.getUserId()) {
                QueryWrapper<UserTeam> userTeamQueryWrapper1 = new QueryWrapper<>();
                userTeamQueryWrapper1.eq("teamId",teamId);
                userTeamQueryWrapper1.last("order by id asc limit 2");
                List<UserTeam> list = userTeamService.list(userTeamQueryWrapper1);
                if (CollectionUtils.isEmpty(list) || list.size()<=1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                Team team = new Team();
                team.setId(teamId);
                team.setUserId(list.get(1).getUserId());
                boolean b = this.updateById(team);
                if (!b) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"队长更新失败");
                }
            }
        }
        //删除退出用户信息
        boolean b = userTeamService.remove(userTeamQueryWrapper2);
        return b;
    }

    /**
     * 队长解散队伍
     * @param requestDelete
     * @param loginUser
     * @return
     */
    @Override
    public boolean deleteTeam(RequestDelete requestDelete, User loginUser) {
        //判断参数是否为空
        if (requestDelete== null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断队伍是否存在
        long id = requestDelete.getId();
        Team byId = this.getById(id);
        if (byId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        //判断是否为队长
        long userId0 = loginUser.getId();
        long userId1 = byId.getUserId();
        if (userId0 != userId1) {
            throw new BusinessException(ErrorCode.NO_AUTH,"没有权限删除队伍");
        }
        //删除队伍
        this.removeById(id);
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId",id);
        userTeamService.remove(userTeamQueryWrapper);
        boolean b = userTeamService.removeById(id);
        return b;
    }

}




