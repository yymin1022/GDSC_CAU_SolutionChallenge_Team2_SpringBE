package google.solution.service;

import google.solution.domain.Mission;
import google.solution.domain.SuccessMission;
import google.solution.dto.*;
import google.solution.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MissionServiceImpl implements MissionService {

    private final MissionRepository missionRepository;

    @Override
    @Transactional(readOnly = true)
    public GetMissionInfoRes getMissionInfo(String code) throws Exception {
        Mission mission = missionRepository.getMissionInfo(code);
        GetMissionInfoRes getMissionInfoRes = GetMissionInfoRes.missionToGetMissionInfoRes(mission);
        return getMissionInfoRes;
    }

    @Override
    public MissionCompleteRes missionComplete(MissionCompleteReq missionCompleteReq, String userId) throws Exception {
        List<SuccessMission> missions = missionRepository.getSuccessMissions(missionCompleteReq, userId);
        if (missions.size() >= 4) {
            missions.add(SuccessMission.missionCompleteReqToSuccessMission(missionCompleteReq));
            double averageScore  = calculateWeightedAverage(missions);
            missionRepository.saveScore(missionCompleteReq.getCode(), averageScore, userId);
            MissionCompleteRes missionCompleteRes = missionRepository.saveMissions(missions, userId);
            return missionCompleteRes;
        } else {
            missions.add(SuccessMission.missionCompleteReqToSuccessMission(missionCompleteReq));
            MissionCompleteRes missionCompleteRes = missionRepository.saveMissions(missions,userId);
            return missionCompleteRes;
        }
    }

    private double calculateWeightedAverage(List<SuccessMission> missions) {
        double averageScore = 0.0;
        for (int i = 0; i < missions.size(); i++) {
            averageScore = averageScore + (i+1) * missions.get(i).getScore();
        }
        averageScore = averageScore / 15;
        averageScore = Double.parseDouble(String.format("%.3f",averageScore));

        return averageScore;
    }

    @Override
    @Transactional(readOnly = true)
    public GetMissionHistoryRes getMissionHistory(String userId) throws Exception {
        GetMissionHistoryRes getMissionHistoryRes = missionRepository.getMissionHistory(userId);
        return getMissionHistoryRes;
    }

    @Override
    @Transactional(readOnly = true)
    public GetCheckListHistoryRes getCheckListHistory(String userId) throws Exception {
        GetCheckListHistoryRes getCheckListHistoryRes = missionRepository.getCheckListHistory(userId);
        return getCheckListHistoryRes;
    }

}