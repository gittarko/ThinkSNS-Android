package com.thinksns.sociax.api;

import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

public interface ApiFriendships {
	static final String SHOW = "show";
	static final String MOD_NAME = "Friendships";
	static final String CREATE = "create";
	static final String DESTROY = "destroy";

	public static final String ADDTOBLACKLIST = "add_blacklist";
	public static final String DELTOBLACKLIST = "remove_blacklist";
	/** 关注话题 */
	public static final String ISFOLLOWTOPIC = "isFollowTopic";
	/** 关注话题 */
	public static final String FOLLOWTOPIC = "followTopic";
	/** 取消关注话题 */
	public static final String UNFOLLOWTOPIC = "unfollowTopic";

	public boolean show(ModelUser friends) throws ApiException, VerifyErrorException;

	public boolean create(ModelUser user) throws ApiException, VerifyErrorException,
			DataInvalidException;

	public boolean destroy(ModelUser user) throws ApiException,
			VerifyErrorException, DataInvalidException;

	public boolean addBlackList(ModelUser user) throws ApiException,
			VerifyErrorException, DataInvalidException;

	public boolean delBlackList(ModelUser user) throws ApiException,
			VerifyErrorException, DataInvalidException;

	/** 是否关注话题 */
	boolean isFollowTopic(ModelUser user, String topic) throws ApiException,
			VerifyErrorException, DataInvalidException;

	/** 关注话题 */
	public boolean followTopic(ModelUser user, String topic) throws ApiException,
			VerifyErrorException, DataInvalidException;

	/** 取消关注话题 */
	public boolean unFollowTopic(ModelUser user, String topic) throws ApiException,
			VerifyErrorException, DataInvalidException;

}
