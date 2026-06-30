package com.niloq.misfinanzas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.niloq.misfinanzas.entity.ProfileEntity;



public interface ProfileRepository extends JpaRepository<ProfileEntity,Long>{

     Optional<ProfileEntity> findByEmail(String email);

     //select * from tlb_profiles where activation_token=?
     Optional<ProfileEntity> findByActivationToken (String activationToken);
    
}
