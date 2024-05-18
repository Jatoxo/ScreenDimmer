package main;

public interface Dimmable {
    void setDim(int level);
    int getDim();
    void addChangeListener(DimListener listener);
}
