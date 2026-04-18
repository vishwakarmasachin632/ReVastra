package com.gl.userservice.client;

import com.gl.userservice.dto.WorkerProfileResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "upcycleservice")
public interface UpcycleWorkerClient {

    @PutMapping("/api/workers/admin/approval-sync/{userId}")
    WorkerProfileResponseDto syncAdminApproval(
            @PathVariable Long userId,
            @RequestParam Boolean approved
    );
}