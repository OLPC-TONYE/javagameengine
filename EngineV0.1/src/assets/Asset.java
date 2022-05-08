package assets;

public abstract class Asset {
	
	public static final Asset NullAsset = new NullAsset();
	
	private String name;
	private int uId;
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

	/**
	 * @return the uId
	 */
	public int getId() {
		return uId;
	}

	/**
	 * @param uId the uId to set
	 */
	public void setId(int uId) {
		this.uId = uId;
	}

	public abstract Asset copy(Asset from);
	
}


class NullAsset extends Asset {

	public NullAsset() {
		super("", AssetType.Null);
	}

	@Override
	public NullAsset copy(Asset from) {
		return this;
		// Nothing to Do
	}
	
}
