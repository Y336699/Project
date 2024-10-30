package com.qian.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qian.usercenter.comment.BaseResponse;
import com.qian.usercenter.comment.ErrorCode;
import com.qian.usercenter.comment.ResultUtils;
import com.qian.usercenter.comment.Utils;
import com.qian.usercenter.job.exception.BusinessException;
import com.qian.usercenter.mapper.TeamMapper;
import com.qian.usercenter.model.domain.Team;
import com.qian.usercenter.model.domain.User;
import com.qian.usercenter.model.domain.UserTeam;
import com.qian.usercenter.model.dto.*;
import com.qian.usercenter.model.request.CreateTeamRequest;
import com.qian.usercenter.model.vo.TeamVO;
import com.qian.usercenter.service.TeamService;
import com.qian.usercenter.service.UserService;
import com.qian.usercenter.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:3000"})
@Slf4j
public class TeamController {
    @Resource
    TeamService teamService;
    @Resource
    TeamMapper teamMapper;
    @Resource
    UserService userService;
    @Resource
    UserTeamService userTeamService;
    @PostMapping("/add")
    public BaseResponse<Long> creatTeam(CreateTeamRequest createTeamRequest, HttpServletRequest request) {
        if (createTeamRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(createTeamRequest,team);
        long teams = teamService.createTeams(team, loginUser);
        return ResultUtils.success(teams);
    }
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(RequestJoin team,HttpServletRequest request) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean b = teamService.joinTeam(team,loginUser);
        return ResultUtils.success(b);
    }
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(RequestQuit requestQuit,HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean b = teamService.quitTeam(requestQuit, loginUser);
        return ResultUtils.success(b);
    }
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(RequestDelete requestDelete, HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean b = teamService.deleteTeam(requestDelete, loginUser);
        return ResultUtils.success(b);
    }
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(RequestUpdate team, HttpServletRequest request) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean b = teamService.updateTeam(team,loginUser);
        return ResultUtils.success(b);
    }
    @GetMapping("/get")
    public BaseResponse<Team> searchTeams(long id) {
        if (id <= 0) {
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team byId = teamService.getById(id);
        return ResultUtils.success(byId);
    }
    @GetMapping("/searchTeamsByName")
    public BaseResponse<Team> searchTeamsByName(String name) {
        if (name == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name",name);
        Team one = teamService.getOne(queryWrapper);
        return ResultUtils.success(one);
    }
    @GetMapping("/list")
    public BaseResponse<List<TeamVO>> searchList(RequestQuery requestQuery,HttpServletRequest httpServletRequest) {
        if (requestQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = Utils.isAdmin(httpServletRequest);
        List<TeamVO> search = teamService.search(requestQuery, isAdmin);
        return ResultUtils.success(search);
    }
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(RequestQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> resultPage = teamService.page(page, queryWrapper);
        return ResultUtils.success(resultPage);
    }
    /**
     * 获取我加入的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamVO>> listMyJoinTeams(RequestQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        // 取出不重复的队伍 id
        // teamId userId
        // 1, 2
        // 1, 3
        // 2, 3
        // result
        // 1 => 2, 3
        // 2 => 3
        Map<Long, List<UserTeam>> listMap = userTeamList.stream()
                .collect(Collectors.groupingBy(UserTeam::getTeamId));
        List<Long> idList = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idList);
        List<TeamVO> teamList = teamService.search(teamQuery, true);
        return ResultUtils.success(teamList);
    }
    /**
     * 获取我创建的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamVO>> listMyCreateTeams(RequestQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<TeamVO> teamList = teamService.search(teamQuery, true);
        return ResultUtils.success(teamList);
    }
}
