package com.fish.fishapp.utils;

import java.util.ArrayList;

public interface MyCallback {
	void done(String object, Exception e);

	void done(ArrayList object, Exception e);
}
