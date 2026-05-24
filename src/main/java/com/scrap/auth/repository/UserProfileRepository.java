package com.scrap.auth.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scrap.auth.entity.User;
import com.scrap.auth.entity.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long>   {

	boolean existsByEmail(String email);
	UserProfile getByUserProfileId(long userProfileId);
	UserProfile getByUser(User user);
	//UserProfile getByEmail(String email);
	Optional<UserProfile> findByEmail(String email);
	
	Optional<UserProfile> findByUserProfileId(Long userProfileId);
}
