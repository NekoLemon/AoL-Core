package cn.catlemon.aol_core.api;

public final class Coordinate<T extends Number> {
	public T x;
	public T y;
	
	public Coordinate(T defaultNum) {
		this.x = this.y = defaultNum;
	}
	
	public Coordinate(T x, T y) {
		this.x = x;
		this.y = y;
	}
}