package com.qian.usercenter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qian.usercenter.comment.BaseResponse;
import com.qian.usercenter.model.domain.Team;
import com.qian.usercenter.model.domain.User;
import com.qian.usercenter.model.dto.*;
import com.qian.usercenter.model.request.CreateTeamRequest;
import com.qian.usercenter.model.vo.TeamVO;

import java.util.List;

/**
* @author Yu
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-03-29 16:36:22
*/
public interface TeamService extends IService<Team> {
    long createTeams(Team createTeamRequest , User userLogin);
    List<TeamVO> search(RequestQuery requestQuery,boolean isAdmin);

    boolean updateTeam(RequestUpdate team, User loginUser);

    boolean joinTeam(RequestJoin team, User loginUser);

    boolean quitTeam(RequestQuit requestQuit,User loginUser);

    boolean deleteTeam(RequestDelete requestDelete,User loginUser);

}
