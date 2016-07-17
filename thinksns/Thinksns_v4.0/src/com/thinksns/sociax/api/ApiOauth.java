package com.thinksns.sociax.api;

import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient.HttpResponseListener;
import com.thinksns.sociax.t4.model.ModelRegister;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;

public interface ApiOauth {
	public static final String MOD_NAME = "Oauth";
	public static final String REQUEST_ENCRYP = "request_key";
	public static final String AUTHORIZE = "authorize";
	public static final String REGISTER = "Register";
	public static final String THRID_LOGIN = "get_other_login_info";
	public static final String BIND_LOGIN = "bind_new_user";
	public static final String UPLPAD_FACE = "register_upload_avatar";

	public static final String CHANGE_FACE = "upload_avatar";

	String SIGN_IN = "signIn";

	public void authorize(final String uname, final String password, 
			final HttpResponseListener listener);

	public Api.Status requestEncrypKey() throws ApiException;

	public Object register(Object data,String...type) throws ApiException;

	public int thirdRegister(Object data) throws ApiException;

	public Object thridLogin(String type, String type_uid) throws ApiException;

	String getRegisterVerifyCode(String phoneNumber);

	String getFindVerifyCode(String phoneNumber);

	Object signIn(ModelRegister data) throws ApiException;
}
