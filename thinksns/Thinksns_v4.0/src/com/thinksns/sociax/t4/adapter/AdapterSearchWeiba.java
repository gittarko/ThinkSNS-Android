package com.thinksns.sociax.t4.adapter;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;

public class AdapterSearchWeiba extends AdapterWeibaList {

    private static final int MAX_COUNT = 5;
    private String name;

    public AdapterSearchWeiba(FragmentSociax fragment, ListData<SociaxItem> list, String name) {
        super(fragment, list);
        this.name = name;
    }

    @Override
    public ListData<SociaxItem> refreshHeader(SociaxItem obj)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        return refreshNew(PAGE_COUNT);
    }

    @Override
    public ListData<SociaxItem> refreshFooter(SociaxItem obj)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        return new Api.WeibaApi().findWeiba(PAGE_COUNT, name, getMaxid(), MAX_COUNT, httpListener);
    }

    @Override
    public ListData<SociaxItem> refreshNew(int count)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        return new Api.WeibaApi().findWeiba(PAGE_COUNT, name, 0, MAX_COUNT, httpListener);
    }
}
