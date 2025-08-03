package com.elec_business.business;

import com.elec_business.entity.AvailabilityRule;

import java.util.List;


public interface AvailabilityRuleBusiness {
    public void createRule(AvailabilityRule rule);
    public List<AvailabilityRule> getRules(String stationId);
    public void deleteRule(String id);
}
