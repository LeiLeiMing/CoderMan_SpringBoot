package com.zhangyu.coderman.service;

import com.zhangyu.coderman.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {
    List<NotificationDTO> list(Integer pageNo, Integer pageSize, Integer id);
}
