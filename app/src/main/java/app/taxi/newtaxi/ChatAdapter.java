package app.taxi.newtaxi;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Data_message> chatData;
    private LayoutInflater inflater;
    private String id;

    public ChatAdapter(){}
    public ChatAdapter(Context applicationContext, int talklist, ArrayList<Data_message> list, String id) {
        this.context = applicationContext;
        this.layout = talklist;
        this.chatData = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.id= id;
    }

    @Override
    public int getCount() { // 전체 데이터 개수
        return chatData.size();
    }

    @Override
    public Object getItem(int position) { // position번째 아이템
        return chatData.get(position);
    }

    @Override
    public long getItemId(int position) { // position번째 항목의 id인데 보통 position
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
//어떤 레이아웃을 만들어 줄 것인지, 속할 컨테이너, 자식뷰가 될 것인지
            convertView = inflater.inflate(layout, parent, false); //아이디를 가지고 view를 만든다
            holder = new ViewHolder();
            holder.img= convertView.findViewById(R.id.PROFILEview);
            holder.tv_msg = convertView.findViewById(R.id.COMMENTtext);
            holder.tv_name = convertView.findViewById(R.id.NAMEtext);
            holder.tv_time = convertView.findViewById(R.id.TIMEtext);
            holder.my_msg = convertView.findViewById(R.id.MYCOMMENTtext);
            holder.my_time = convertView.findViewById(R.id.MYTIMEtext);

            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }

//누군지 판별
        if(chatData.get(position).getId().equals(id)){
            holder.tv_time.setVisibility(View.GONE);
            holder.tv_name.setVisibility(View.GONE);
            holder.tv_msg.setVisibility(View.GONE);
            holder.img.setVisibility(View.GONE);

            holder.my_msg.setVisibility(View.VISIBLE);
            holder.my_time.setVisibility(View.VISIBLE);

            holder.my_time.setText(chatData.get(position).getTime());
            holder.my_msg.setText(chatData.get(position).getComment());
            Log.e("INDEX1",chatData.get(position).getTime());
            Log.e("INDEX1",chatData.get(position).getComment());
        }else{
            holder.tv_time.setVisibility(View.VISIBLE);
            holder.tv_name.setVisibility(View.VISIBLE);
            holder.tv_msg.setVisibility(View.VISIBLE);
            holder.img.setVisibility(View.VISIBLE);

            holder.my_msg.setVisibility(View.GONE);
            holder.my_time.setVisibility(View.GONE);

            Glide.with(convertView)
                    .load(chatData.get(position).getPROFILEURL())
                    .into(holder.img);
            holder.tv_msg.setText(chatData.get(position).getComment());
            holder.tv_time.setText(chatData.get(position).getTime());
            holder.tv_name.setText(chatData.get(position).getUsername());
            Log.e("INDEX",chatData.get(position).getUsername());
        }
        return convertView;
    }

    //뷰홀더패턴
    public class ViewHolder{
        ImageView img;
        TextView tv_msg;
        TextView tv_time;
        TextView tv_name;
        TextView my_time;
        TextView my_msg;
    }

}