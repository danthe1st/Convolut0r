package io.github.danthe1st.convolut0r.model;

public record Convolution(double[][][] convolution, int striding, int padding) {// NOSONAR I don't care about hashCode/equals
	public Convolution
	
	{
		if(convolution == null){
			throw new IllegalArgumentException();
		}
		if(striding < 1){
			throw new IllegalArgumentException();
		}
		if(padding < 0){
			throw new IllegalArgumentException();
		}
	}
	
	public Convolution(double[][][] convolution) {
		this(convolution, 1, 0);
	}
	
	public Convolution(double[][] convolution) {
		this(new double[][][] { convolution }, 1, 0);
	}
	
	public Convolution(double[][] convolution, int striding, int padding) {
		this(new double[][][] { convolution }, striding, padding);
	}
	
	public int[][][] convolute(int[][][] pixels) {
//		double convMean = 0;
//		for(double[][] channel : convolution){
//			for(double[] row : channel){
//				for(double cell : row){
//					convMean += cell;
//				}
//			}
//		}
		// convMean = (255 * convMean) / (convolution.length * convolution[0].length *
		// convolution[0][0].length);
		
//		double divAll = convMean == 0 ? 1 : convMean / 3;
		
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		int newWidth = (int) Math.floor((pixels[0].length + 2 * padding - convolution[0].length) / (double) striding) + 1;
		int newHeight = (int) Math.floor((pixels[0][0].length + 2 * padding - convolution[0][0].length) / (double) striding) + 1;
		int[][][] ret = new int[pixels.length][newWidth][newHeight];
		for(int channel = 0; channel < ret.length; channel++){
			for(int oldI = -padding, newI = 0; oldI + convolution[0].length - 1 < pixels[0].length + padding; oldI += striding, newI++){
				for(int oldJ = -padding, newJ = 0; oldJ + convolution[0][0].length - 1 < pixels[0][0].length + padding; oldJ += striding, newJ++){
					double sum = 0;
					for(int i = 0; i < convolution[0].length; i++){
						for(int j = 0; j < convolution[0][i].length; j++){
							int oldX = oldI + i;
							int oldY = oldJ + j;
							if(oldX < 0 || oldX >= pixels[0].length || oldY < 0 || oldY >= pixels[0][0].length){
								sum += 0;// padding
							}else{
								sum += pixels[channel][oldX][oldY] * convolution[channel][i][j];
							}
						}
					}
					ret[channel][newI][newJ] = (int) (sum /* / ((double) convolution[0].length * convolution[0].length) */);
				}
			}
		}
		for(int i = 0; i < pixels.length; i++){
			for(int j = 0; j < pixels[0].length; j++){
				for(int k = 0; k < pixels[0][0].length; k++){
					pixels[i][j][k] = Math.max(0, Math.min(0xFF, pixels[i][j][k] /* (255 * (pixels[i][j][k] - min) / (max - min)) */));
				}
			}
		}
		
		return ret;
	}
}
