package od.team.playpiano;

import android.content.ContentValues;
import android.os.AsyncTask;

public class NetWorkAsync extends AsyncTask<Void, Void, String> {

    String url;
    ContentValues values;

    public NetWorkAsync(String url, ContentValues values) {
        this.url = url;
        this.values = values;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //progress bar를 보여주는 등등의 행위
    }

    @Override
    protected String doInBackground(Void... params) {
        String result;
        HttpConnection httpConnection = new HttpConnection();
        result = httpConnection.request(url, values);
        return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
    }

}
