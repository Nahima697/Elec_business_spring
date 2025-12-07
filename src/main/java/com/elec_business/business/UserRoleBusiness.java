package com.elec_business.business;


public interface UserRoleBusiness {

   void addRoleOwner( String id);
   void addRoleRenter( String id);
   void removeRole(String userId, String roleName);

    }
