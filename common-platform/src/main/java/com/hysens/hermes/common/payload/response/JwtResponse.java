package com.hysens.hermes.common.payload.response;

import com.hysens.hermes.common.model.enums.MessengerEnum;

import java.util.List;

public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private Long id;
	private String username;
	private String email;
	private Long partnerId;
	private List<String> roles;
	private List<MessengerEnum> messengerPriority;

	public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles, Long partnerId, List<MessengerEnum> messengerPriority) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
		this.partnerId = partnerId;
		this.messengerPriority = messengerPriority;
	}

	public Long getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(Long partnerId) {
		this.partnerId = partnerId;
	}

	public String getAccessToken() {
		return token;
	}

	public void setAccessToken(String accessToken) {
		this.token = accessToken;
	}

	public String getTokenType() {
		return type;
	}

	public void setTokenType(String tokenType) {
		this.type = tokenType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public List<MessengerEnum> getMessengerPriority() {
		return messengerPriority;
	}

	public void setMessengerPriority(List<MessengerEnum> messengerPriority) {
		this.messengerPriority = messengerPriority;
	}
}
