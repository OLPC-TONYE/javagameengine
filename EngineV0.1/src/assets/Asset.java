package assets;

public abstract class Asset {
	
	public static final Asset NullAsset = new NullAsset();
	
	private String name;
	private AssetType type;
	
	public Asset(String name, AssetType type) {
		this.name = name;
		this.type = type;
	}

	public String getAssetName() {
		return name;
	}
	
	public AssetType getAssetType() {
		return type;
	}
	
}


class NullAsset extends Asset {

	public NullAsset() {
		super("", AssetType.Null);
	}
	
}
