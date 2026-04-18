package com.gl.rewardservice;

import com.gl.rewardservice.client.UserServiceClient;
import com.gl.rewardservice.dto.*;
import com.gl.rewardservice.entity.Reward;
import com.gl.rewardservice.enums.RewardSource;
import com.gl.rewardservice.exception.InsufficientPointsException;
import com.gl.rewardservice.repository.RewardRepository;
import com.gl.rewardservice.service.impl.RewardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class RewardServiceImplTest {
    @Mock
    private RewardRepository rewardRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private RewardServiceImpl rewardService;
    @Test
    void creditPoints_ShouldIncreaseBalance() {
        when(rewardRepository.findTopByUserIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.empty());
        when(rewardRepository.save(any(Reward.class))).thenAnswer(inv ->
                inv.getArgument(0));
        RewardResponseDto response = rewardService.creditPoints(
                RewardCreditRequestDto.builder()
                        .userId(1L)
                        .points(50)
                        .source(RewardSource.DONATION)
                        .note("Donation reward")
                        .build()
        );
        assertEquals(50, response.getBalance());
        assertEquals(50, response.getPointsEarned());
    }
    @Test
    void redeemPoints_ShouldThrow_WhenInsufficient() {
        when(rewardRepository.findTopByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(Optional.of(Reward.builder().balance(10).build()));
        assertThrows(InsufficientPointsException.class, () ->
                rewardService.redeemPoints(
                        RewardRedeemRequestDto.builder().userId(1L).pointsToRedeem(50).build()
                ));
    }
    @Test
    void getMyRewards_ShouldReturnSummary() {
        // 1. Setup the User Profile mock
        when(userServiceClient.getCurrentUserProfile())
                .thenReturn(UserResponseDto.builder().id(1L).build());

        // 2. Setup the Reward Repository mock
        when(rewardRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(
                        Reward.builder()
                                .userId(1L)
                                .pointsEarned(50)
                                .pointsRedeemed(10)
                                .balance(40)
                                .source(RewardSource.DONATION)
                                .createdAt(LocalDateTime.now())
                                .build()
                ));

        // 3. Execute
        RewardSummaryDto summary = rewardService.getMyRewards();

        // 4. Assert
        assertEquals(40, summary.getCurrentBalance());
        assertEquals(50, summary.getTotalEarned());
        assertEquals(10, summary.getTotalRedeemed());
    }
}
