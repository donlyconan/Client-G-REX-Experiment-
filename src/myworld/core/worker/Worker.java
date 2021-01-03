package myworld.core.worker;

public interface Worker {

    public <T extends Object> T work();

}
