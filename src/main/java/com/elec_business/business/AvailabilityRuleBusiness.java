package com.elec_business.business;

import com.elec_business.entity.AvailabilityRule;
import com.elec_business.entity.ChargingStation;
import com.elec_business.entity.User;

import java.util.List;


public interface AvailabilityRuleBusiness {
    public void createRule(AvailabilityRule rule,  User currentUser);
    public List<AvailabilityRule> getRules(String stationId);
    public void deleteRule(String id);
}
