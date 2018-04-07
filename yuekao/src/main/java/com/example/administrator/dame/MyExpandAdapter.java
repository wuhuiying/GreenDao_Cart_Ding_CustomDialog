package com.example.administrator.dame;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/4/7.
 */

public class MyExpandAdapter extends BaseExpandableListAdapter {
    MyCartBean myCartBean;
    Context context;
    Handler handler;
    ShowPresenter sp;
    public MyExpandAdapter(MyCartBean myCartBean, Context context, Handler handler, ShowPresenter sp) {
        this.myCartBean = myCartBean;
        this.context = context;
        this.handler=handler;
        this.sp=sp;
    }

    @Override
    public int getGroupCount() {
        return myCartBean.getData().size();
    }

    @Override
    public int getChildrenCount(int i) {
        return myCartBean.getData().get(i).getList().size();
    }

    @Override
    public Object getGroup(int i) {
        return myCartBean.getData().get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return myCartBean.getData().get(i).getList().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int i, boolean b, View view, ViewGroup viewGroup) {
        //
        final GroupHolder groupHolder;
        if(view==null){
            view=View.inflate(context, R.layout.group_item,null);
            groupHolder=new GroupHolder();
            groupHolder.check_group=view.findViewById(R.id.check_group);
            groupHolder.text_group=view.findViewById(R.id.text_group);
            //
            view.setTag(groupHolder);
        }else{
            groupHolder= (GroupHolder) view.getTag();
        }
        //赋值
        groupHolder.check_group.setChecked(myCartBean.getData().get(i).isGroupChecked());
        groupHolder.text_group.setText(myCartBean.getData().get(i).getSellerName());
        //组的ck的点击事件
        groupHolder.check_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAllchildsByGuoupCk(myCartBean.getData().get(i).getList(),groupHolder.check_group.isChecked());
            }
        });

        return view;
    }
    /*
    * 根据组的ck改变
    * 调用接口
    * */
    private void updateAllchildsByGuoupCk(List<MyCartBean.DataBean.ListBean> list, boolean checked) {
        Observable[] obsers=new Observable[list.size()];

        for (int i=0;i<list.size();i++){
            Map<String, String> params = new HashMap<>();
            params.put("uid","2776");
            params.put("sellerid", list.get(i).getSellerid() + "");
            params.put("pid", list.get(i).getPid() + "");
            params.put("selected", String.valueOf(checked ? 1:0));
            params.put("num", String.valueOf(list.get(i).getNum()));
            obsers[i]= RetrofitHelper.getApiService(Api.base_url).post(Api.update,params);
        }
        Observable.merge(obsers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {
                        sp.getData(Api.base_url);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });

    }


    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ChildHolder childHolder;
        Log.d("++++++","3");
        if(view==null){
            view=View.inflate(context,R.layout.child_item,null);
            childHolder=new ChildHolder();
            childHolder.check_child= view.findViewById(R.id.check_child);
            childHolder.text_price = view.findViewById(R.id.text_price);
            childHolder.image_good=view.findViewById(R.id.image_good);
            childHolder.text_add=view.findViewById(R.id.text_add);
            childHolder.text_jian=view.findViewById(R.id.text_jian);
            childHolder.text_num=view.findViewById(R.id.text_num);
            childHolder.text_title=view.findViewById(R.id.text_title);
            view.setTag(childHolder);
        }else{
            childHolder= (ChildHolder) view.getTag();
        }

        //赋值
        final MyCartBean.DataBean.ListBean listBean = myCartBean.getData().get(i).getList().get(i1);

        childHolder.text_num.setText(listBean.getNum() + "");//......注意
        childHolder.text_price.setText("¥" + listBean.getBargainPrice());
        childHolder.text_title.setText(listBean.getTitle());
        //设置checkBox选中状态
        childHolder.check_child.setChecked(listBean.getSelected() == 0 ? false : true);
        //设置图片显示
        String uri = listBean.getImages().split("\\|")[0];
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();
        childHolder.image_good.setController(controller);
        //子条目checkbox的点击事件
        childHolder.check_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateChildByck(listBean);
            }
        });
        //加减的点击事件
        childHolder.text_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateChildByNum(listBean,true);
            }
        });
        childHolder.text_jian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listBean.getNum() == 1) {
                    Toast.makeText(context,"数量不能再减啦！",Toast.LENGTH_SHORT).show();
                    return;
                }
                updateChildByNum(listBean,false);
            }
        });
        return view;
    }

    private void updateChildByNum(MyCartBean.DataBean.ListBean listBean, boolean b) {
        Map<String, String> params =new HashMap<>();
        params.put("uid", 2776 + "");
        params.put("sellerid", listBean.getSellerid() + "");
        params.put("pid", listBean.getPid() + "");
        params.put("selected", String.valueOf(listBean.getSelected()));
        if (b) {
            params.put("num", String.valueOf(listBean.getNum() + 1));
        } else {
            params.put("num", String.valueOf(listBean.getNum() - 1));
        }

        RetrofitHelper.getApiService(Api.base_url).post("product/updateCarts",params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        //查询
                        sp.getData(Api.base_url);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {

                    }
                });

    }

    /*
    * 根据子条目的ck状态
    * 调更新购物车的接口
    * */
    private void updateChildByck(MyCartBean.DataBean.ListBean listBean) {
        Log.d("aaaaaaa",listBean.getPid()+"");
        Map<String, String> params =new HashMap<>();
        params.put("uid","2776");
        params.put("sellerid", listBean.getSellerid() + "");
        params.put("pid", listBean.getPid() + "");
        params.put("selected", String.valueOf(listBean.getSelected()== 1 ? 0 : 1));
        params.put("num", String.valueOf(listBean.getNum()));

        RetrofitHelper.getApiService(Api.base_url).post("product/updateCarts",params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        //查询
                        sp.getData(Api.base_url);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {

                    }
                });


    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
    /*
    * 价格和数量的改变
    * */
    public void sendPriceAndCount() {
        //初始化数量和价格
        int count=0;
        double price=0;

        for(int i=0;i<myCartBean.getData().size();i++){
            for (int j=0;j<myCartBean.getData().get(i).getList().size();j++){
                //判断 选中的才计算价格和数量
                if(myCartBean.getData().get(i).getList().get(j).getSelected()==1){
                    count+=myCartBean.getData().get(i).getList().get(j).getNum();
                    price+=myCartBean.getData().get(i).getList().get(j).getBargainPrice() * myCartBean.getData().get(i).getList().get(j).getNum();
                }
            }
        }
        //发送...显示
        Message msg = Message.obtain();
        msg.what = 0;
        msg.obj = price;
        msg.arg1 = count;

        handler.sendMessage(msg);
    }
    /*
    * 根据全选的状态改变组和子的状态
    * 调更新接口
    * */
    public void isCheckAllChecked(boolean checked) {
        //创建集合 装所有子类
        List<MyCartBean.DataBean.ListBean> allList=new ArrayList<>();
        for(int i=0;i<myCartBean.getData().size();i++){
            myCartBean.getData().get(i).setGroupChecked(checked);
            Log.d("++++++++","++++++"+myCartBean.getData().get(i).isGroupChecked());

            for(int j=0;j<myCartBean.getData().get(i).getList().size();j++){
                myCartBean.getData().get(i).getList().get(j).setSelected(checked ? 1:0);
                Log.d("+++","++++++"+myCartBean.getData().get(i).getList().get(j).getSelected());
                allList.add(myCartBean.getData().get(i).getList().get(j));
            }

        }
        //更新所有数据
        updateAllhilds(allList);


    }
    /*
    * 根据全选状态更新组和子条目
    * */
    private void updateAllhilds(List<MyCartBean.DataBean.ListBean> allList) {
        //创建被观察者数组
        Observable[] observables=new Observable[allList.size()];
        //遍历数组
        for(int i=0;i<allList.size();i++){
            Map<String, String> params = new HashMap<>();
            params.put("uid", 2776 + "");
            params.put("sellerid", allList.get(i).getSellerid() + "");
            params.put("pid", allList.get(i).getPid() + "");
            params.put("selected", String.valueOf(allList.get(i).getSelected()));
            params.put("num", String.valueOf(allList.get(i).getNum()));

            Log.d("=====",allList.get(i).getSelected() + "++++++");
            observables[i]= RetrofitHelper.getApiService(Api.base_url).post("product/updateCarts",params);
        }
        Observable.merge(observables)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer() {
                    @Override
                    public void onCompleted() {
                        //调查询接口
                        sp.getData(Api.base_url);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
        // notifyDataSetChanged();
    }


    private class GroupHolder {
        CheckBox check_group;
        TextView text_group;
    }

    private class ChildHolder {
        CheckBox check_child;
        SimpleDraweeView image_good;
        TextView text_title;
        TextView text_price;
        TextView text_jian;
        TextView text_num;
        TextView text_add;
    }
}
