package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.entity.ProductInfo;
import com.tbea.tb.tbeawaterelectrician.fragment.nearby.NearbyCommodithFragment;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * 购物车
 */

public class ShoppingCartActivity extends TopActivity implements View.OnClickListener,BGARefreshLayout.BGARefreshLayoutDelegate {
    private ListView mListView;
    private MyAdapter mAdapter;
    private Context mContext;
    private BGARefreshLayout mRefreshLayout;
    private List<String> mSelectIds = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart_list);
        mContext = this;
        initTopbar("购物车","编辑",this);
        initUI();
        listener();
    }

    public  void initUI(){
        mListView = (ListView)findViewById(R.id.listview);
        mAdapter = new MyAdapter(mContext);
        mListView.setAdapter(mAdapter);
        mRefreshLayout = (BGARefreshLayout)findViewById(R.id.rl_recyclerview_refresh);
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(mContext, true));
        virtualData();
    }

    public  void listener(){
        ((CheckBox)findViewById(R.id.all_chekbox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    mAdapter.selectAll();
                    ((CheckBox)findViewById(R.id.all_chekbox)).setText("全不选");

                }else {
                    mAdapter.selectAllNo();
                    ((CheckBox)findViewById(R.id.all_chekbox)).setText("全选");
                }
            }
        });
    }

    /**
     * 模拟数据<br>
     * 遵循适配器的数据列表填充原则，组元素被放在一个List中，对应的组元素下辖的子元素被放在Map中，<br>
     * 其键是组元素的Id(通常是一个唯一指定组元素身份的值)
     */
    private void virtualData() {
            List<ProductInfo> products = new ArrayList<>();
            for (int i = 0; i <= 7; i++) {
                products.add(new ProductInfo(i + "", "商品", "", "第" + i + "个商品", 120.00 + i, 1));
            }
          mAdapter.addAll(products);

    }

    /**
     * 设置结算数量和金额
     * @param count
     * @param price
     */
    public  void setPayCount(int count,Double price){
        ((TextView)findViewById(R.id.tv_go_to_pay)).setText("去结算("+count+")");
        ((TextView)findViewById(R.id.tv_total_price)).setText("￥"+price);
    }

    @Override
    public void onClick(View view) {
        TextView textView = (TextView)view;
        if("编辑".equals(textView.getText())){
            textView.setText("完成");
            findViewById(R.id.tv_go_to_pay_layout).setVisibility(View.GONE);
            findViewById(R.id.shop_car_delect_layout).setVisibility(View.VISIBLE);
        }else {
            textView.setText("编辑");
            findViewById(R.id.tv_go_to_pay_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.shop_car_delect_layout).setVisibility(View.GONE);
        }

    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //下拉刷新
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        //上拉加载更多
        return false;
    }

    private class MyAdapter extends BaseAdapter{

        private Context mContext;
        private List<ProductInfo> mList = new ArrayList<>();

        public MyAdapter (Context context){
            mContext = context;
        }
        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            final ChildHolder cholder;
            if(convertView == null){
                cholder = new ChildHolder();
                convertView = (View)inflater.inflate(R.layout.activity_shoping_cart_list_item,null);
                cholder.cb_check = (CheckBox) convertView.findViewById(R.id.check_box);
                cholder.tv_count = (TextView) convertView.findViewById(R.id.tv_num);
                cholder.iv_increase = (TextView) convertView.findViewById(R.id.tv_add);
                cholder.iv_decrease = (TextView) convertView.findViewById(R.id.tv_reduce);
                convertView.setTag(cholder);
            }else {
                // convertView = childrenMap.get(groupPosition);
                cholder = (ChildHolder) convertView.getTag();
            }

            final ProductInfo product =  mList.get(i);

            cholder.cb_check.setChecked(product.isChoosed());
            cholder.cb_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    product.setChoosed(((CheckBox) v).isChecked());
                    cholder.cb_check.setChecked(((CheckBox) v).isChecked());
                    Double price = product.getPrice()*product.getCount();

                    if(((CheckBox) v).isChecked()){
                        mSelectIds.add(product.getId());
                    }else {
                        mSelectIds.remove(product.getId());
                    }
                    setPayCount(mSelectIds.size(),price);
                }
            });
            cholder.iv_increase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int count = product.getCount();
                    cholder.tv_count.setText(++count+"");
                    product.setCount(count);
                    if(product.isChoosed()){
                        Double price = product.getPrice()*count;
                        setPayCount(mSelectIds.size(),price);
                    }
                }
            });

            cholder.iv_decrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int count = product.getCount();
                    cholder.tv_count.setText(--count+"");
                    product.setCount(count);
                    if(product.isChoosed()){
                        Double price = product.getPrice()*count;
                        setPayCount(mSelectIds.size(),price);
                    }
                }
            });

            return convertView;
        }

        private void addAll(List<ProductInfo> list){
            mList.addAll(list);
            notifyDataSetChanged();
        }

        public void selectAll(){
            if(mList != null && mList.size()>0){
                Double price = 0.0;
                for (ProductInfo item:mList) {
                    item.setChoosed(true);
                    mSelectIds.add(item.getId());
                    int count = item.getCount();
                    price = price + (count*item.getPrice());
                }
                setPayCount(mSelectIds.size(),price);
                notifyDataSetChanged();
            }
        }

        public void selectAllNo(){
            if(mList != null && mList.size()>0){
                for (ProductInfo item:mList) {
                    item.setChoosed(false);
                   mSelectIds.clear();
                }
                setPayCount(mSelectIds.size(),0.0);
                notifyDataSetChanged();
            }

        }

        /**
         * 子元素绑定器
         */
        private class ChildHolder {
            CheckBox cb_check;
            TextView tv_product_name;
            TextView tv_product_desc;
            TextView tv_price;
            TextView iv_increase;
            TextView tv_count;
            TextView iv_decrease;
            TextView tv_delete;
        }
    }
}
