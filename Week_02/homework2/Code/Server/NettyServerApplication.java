public class NettyServerApplication {

    public static void main(String[] args) {
        HttpServer server = new HttpServer(false,8808);
        try {
            System.out.println("server running!!!");
            server.run();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
