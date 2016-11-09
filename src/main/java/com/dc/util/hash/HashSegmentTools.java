package com.dc.util.hash;


/**
 * 根据对象hashCode获得对象大致平均的分布在不同的分段中（参考java.util.concurrent.ConcurrentHashMap）
 * 
 * @author Daemon
 *
 */
public class HashSegmentTools {

	/**
     * The maximum number of segments to allow; used to bound
     * constructor arguments. Must be power of two less than 1 << 24.
     */
	protected static final int MAX_SEGMENTS = 1 << 16; // slightly conservative
    
    /**
     * Mask value for indexing into segments. The upper bits of a
     * key's hash code are used to choose the segment.
     */
    protected final int segmentMask;

    /**
     * Shift value for indexing within segments.
     */
    protected final int segmentShift;

	/**
	 * @param concurrencyLevel 分段的数量（必须是2的n次方）
	 */
	public HashSegmentTools(int concurrencyLevel) {
		
		if (concurrencyLevel <= 0)
            throw new IllegalArgumentException();
		
		if (concurrencyLevel > MAX_SEGMENTS)
            concurrencyLevel = MAX_SEGMENTS;
		
		// Find power-of-two sizes best matching arguments
        int sshift = 0;
        int ssize = 1;
        while (ssize < concurrencyLevel) {
            ++sshift;
            ssize <<= 1;
        }
        this.segmentShift = 32 - sshift;
        this.segmentMask = ssize - 1;
        
	}
	
	/**
	 * 对象k落在哪个分段中
	 * 
	 * @param k 目标对象
	 * @return 对象k落在哪个分段中
	 */
	public int getSegmentIndex(Object k) {
		
		int h = k.hashCode();

        // Spread bits to regularize both segment and index locations,
        // using variant of single-word Wang/Jenkins hash.
        h += (h <<  15) ^ 0xffffcd7d;
        h ^= (h >>> 10);
        h += (h <<   3);
        h ^= (h >>>  6);
        h += (h <<   2) + (h << 14);
		h = h ^ (h >>> 16);
		
		return (h >>> segmentShift) & segmentMask;
	}
	
}
