package com.elec_business.business;

import com.elec_business.controller.dto.AvailabilityRuleDto;
import com.elec_business.entity.AvailabilityRule;
import com.elec_business.entity.ChargingStation;
import com.elec_business.entity.User;

import java.util.List;


public interface AvailabilityRuleBusiness {
    public void createRule(AvailabilityRuleDto rule,  User currentUser);
    public List<AvailabilityRuleDto> getRules(String stationId);
    public void deleteRule(String id);
}
