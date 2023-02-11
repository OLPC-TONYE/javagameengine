package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dictionary<K, L, M, N> {
	
	private List<K> keyset = new ArrayList<>();
	
	private Map<K, L> map1 = new HashMap<>();
	private Map<K, M> map2 = new HashMap<>();
	private Map<K, N> map3 = new HashMap<>();
	
	public Dictionary() {
	}
	
	public void put(K reference, L object1, M object2, N object3) {
		keyset.add(reference);
		map1.put(reference, object1);
		map2.put(reference, object2);
		map3.put(reference, object3);
	}
	
	public void remove(K reference) {
		keyset.remove(reference);
		map1.remove(reference);
		map2.remove(reference);
		map3.remove(reference);
	}
	
	public L getFromFirstMap(K reference) {
		return map1.get(reference);
	}
	
	public Map<K, L> getFirstMap() {
		return map1;
	}
	
	public M getFromSecondMap(K reference) {
		return map2.get(reference);
	}
	
	public Map<K, M> getSecondMap() {
		return map2;
	}
	
	public N getFromThirdMap(K reference) {
		return map3.get(reference);
	}
	
	public Map<K, N> getThirdMap() {
		return map3;
	}
	
	public List<K> keySet() {
		return keyset;
	}
	
	public boolean containsKey(K reference) {
		return keyset.contains(reference);
	}

	public void clear() {
		keyset.clear();
		map1.clear();
		map2.clear();
		map3.clear();
	}
	
}
