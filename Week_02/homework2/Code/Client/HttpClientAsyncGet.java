import okhttp3.*;
import java.io.IOException;

public class HttpClientAsyncGet {
    private OkHttpClient client = null;

    static class RespHandler {
        public void handle(String rsp_str) {
            System.out.println(rsp_str);
        }
    }

    static class RespCallback implements Callback {
        private final RespHandler handler;

        public RespCallback(RespHandler h) {
            handler = h;
        }

        public void onFailure(Call call, IOException e) {
            e.printStackTrace();
        }

        public void onResponse(Call call, Response response) throws IOException {
            handler.handle(response.body().string());
        }
    }

    public HttpClientAsyncGet() {
        client = new OkHttpClient();
    }

    public void getRequest(String url, RespHandler handler) {
        Request req = new Request.Builder().url(url).build();
        client.newCall(req).enqueue(new RespCallback(new RespHandler()));

    }

    public static void main(String[] args) {
        HttpClientSyncGet client = new HttpClientSyncGet();
        System.out.println(client.getRequest("http://localhost:8808/api/test"));
    }
}
