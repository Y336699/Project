package com.qian.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qian.usercenter.model.domain.Tag;
import com.qian.usercenter.mapper.TagMapper;
import com.qian.usercenter.service.TagService;
import org.springframework.stereotype.Service;

/**
* @author Yu
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2024-03-14 21:34:12
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




