package com.elec_business.business;

import com.elec_business.entity.UserRole;
import com.elec_business.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


public interface UserRoleBusiness {

   void addRoleOwner( String id);
   void addRoleRenter( String id);
}
