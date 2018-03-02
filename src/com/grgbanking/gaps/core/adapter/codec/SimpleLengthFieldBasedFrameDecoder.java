package com.grgbanking.gaps.core.adapter.codec;

import java.nio.ByteOrder;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class SimpleLengthFieldBasedFrameDecoder extends LengthFieldBasedFrameDecoder {

	private LengthFieldFormat lengthFieldFormat;

	public SimpleLengthFieldBasedFrameDecoder(
			LengthFieldFormat lengthFieldFormat,
			int maxFrameLength,
			int lengthFieldOffset,
			int lengthFieldLength,
			int lengthAdjustment,
			int initialBytesToStrip) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
		this.lengthFieldFormat = lengthFieldFormat;
	}

	@Override
	protected long getUnadjustedFrameLength(ByteBuf buf, int offset, int length, ByteOrder order) {

		if (lengthFieldFormat == LengthFieldFormat.BIN)
			return super.getUnadjustedFrameLength(buf, offset, length, ByteOrder.BIG_ENDIAN);

		byte[] lengthBytes = new byte[length];
		buf.getBytes(offset, lengthBytes, 0, length);

		if (lengthFieldFormat == LengthFieldFormat.BCD)
			return getFrameLengthBCD(lengthBytes);

		if (lengthFieldFormat == LengthFieldFormat.DEC)
			return getFrameLengthDEC(lengthBytes);

		if (lengthFieldFormat == LengthFieldFormat.HEX)
			return getFrameLengthHEX(lengthBytes);

    	throw new DecoderException(
    		"unsupported lengthFieldFormat: "
    		+ lengthFieldFormat
    		+ " (expected: BCD, BIN, DEC, HEX)");		
	}

	static private long getFrameLengthBCD(byte[] lengthBytes) {
		long result = 0;
		for (int i = 0; i < lengthBytes.length; i++) {
			result = result * 100 + ((lengthBytes[i] & 0xF0) >>> 4) * 10 + (lengthBytes[i] & 0x0F); 
		}
		return result;
	}

	static private long getFrameLengthHEX(byte[] lengthBytes) {
		return Long.parseUnsignedLong(new String(lengthBytes), 16);
	}	

	static private long getFrameLengthDEC(byte[] lengthBytes) {
		return Long.parseUnsignedLong(new String(lengthBytes));
	}
	
}
