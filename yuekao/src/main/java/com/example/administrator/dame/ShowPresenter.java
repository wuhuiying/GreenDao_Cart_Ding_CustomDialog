package com.example.administrator.dame;

/**
 * Created by Administrator on 2018/4/7.
 */

public class ShowPresenter implements ShowPImp {
    ShowVImp showVImp;

    public ShowPresenter(ShowVImp showVImp) {
        this.showVImp = showVImp;
    }

    /*
     * 调model层方法
     * */
    public void getData(String base_url) {
        ShowModel sm = new ShowModel(this);
        sm.getData(base_url);

    }

    @Override
    public void onSuccess(MyCartBean myCartBean) {
        showVImp.onSuccess(myCartBean);
    }

    @Override
    public void onError() {

    }
}
