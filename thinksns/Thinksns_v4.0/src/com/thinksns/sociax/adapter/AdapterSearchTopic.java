package com.thinksns.sociax.adapter;

import com.thinksns.sociax.t4.adapter.AdapterPostRecommendList;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;

public class AdapterSearchTopic extends AdapterPostRecommendList {

    private String name;

    public AdapterSearchTopic(FragmentSociax fragment, ListData<SociaxItem> list, String name) {
        super(fragment, list);
        this.name = name;
    }

    @Override
    public ListData<SociaxItem> refreshHeader(SociaxItem obj)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        return refreshNew(0);
    }

    @Override
    public ListData<SociaxItem> refreshFooter(SociaxItem obj)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
//        return getApi().searchTopic(name, httpListener);
        return null;
    }

    @Override
    public ListData<SociaxItem> refreshNew(int count)
            throws VerifyErrorException, ApiException, ListAreEmptyException{
        return getApi().searchTopic(name, httpListener);
    }
}
