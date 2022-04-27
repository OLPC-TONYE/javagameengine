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
	
	public void setAssetName(String name) {
		this.name = name;
	}

	public void setAssetType(AssetType type) {
		this.type = type;
	}

	public abstract void copy(Asset from);
	
}


class NullAsset extends Asset {

	public NullAsset() {
		super("", AssetType.Null);
	}

	@Override
	public void copy(Asset from) {
		// Nothing to Do
	}
	
}
