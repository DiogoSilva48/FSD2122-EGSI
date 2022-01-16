package ST;

public class ServicoRedeSockets {
    
    private String description;
    private String ip;
    private int port;
    private String register_key;

    public ServicoRedeSockets(String description, String ip, int port, String register_key){
        this.description = description;
        this.ip = ip;
        this.port = port;
        this.register_key = register_key;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public String getRegister_key() {
        return register_key;
    }
    public void setRegister_key(String register_key) {
        this.register_key = register_key;
    }

}
