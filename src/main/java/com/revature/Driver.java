package com.revature;

import java.util.List;

import com.revature.models.User;
import com.revature.orm.ObjectMapper;

public class Driver {
	
	public static void main(String[] args) {
		ObjectMapper om = new ObjectMapper();
		User fonzi = new User(3, "fonzi", "garza", "STUDENT", "pass3");
		om.delete(fonzi);
		List<User> users = om.selectAll(User.class);
		users.forEach(x -> System.out.println(x));
	}

}
