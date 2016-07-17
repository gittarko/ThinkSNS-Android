package com.thinksns.sociax.api;

import com.thinksns.sociax.modle.ApproveSite;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;

public interface ApiSites {
	public static final String MOD_NAME = "Sitelist";
	public static final String GET_SITE_LIST = "getSiteList";
	public static final String GET_SITE_STATUS = "getSiteStatus";

	public ListData<SociaxItem> getSisteList() throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException;

	public ListData<SociaxItem> newSisteList(int count) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException;

	public ListData<SociaxItem> getSisteListHeader(ApproveSite as, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<SociaxItem> getSisteListFooter(ApproveSite as, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public boolean getSiteStatus(ApproveSite as) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException;

	public boolean isSupport() throws ApiException, ListAreEmptyException,
			DataInvalidException, VerifyErrorException;

	public boolean isSupportReg() throws ApiException, ListAreEmptyException,
			DataInvalidException, VerifyErrorException;

	ListData<SociaxItem> searchSisteList(String key, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;
}
