package com.grgbanking.gaps.core.adapter.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.LengthFieldPrepender;

public class SimpleLengthFieldPrepender extends LengthFieldPrepender {

	private LengthFieldFormat lengthFieldFormat;
    private final int lengthFieldLength;
    private final boolean lengthIncludesLengthFieldLength;
    private final int lengthAdjustment;

	public SimpleLengthFieldPrepender(
			LengthFieldFormat lengthFieldFormat,
			int lengthFieldLength,
			int lengthAdjustment,
			boolean lengthIncludesLengthFieldLength) {
		super(lengthFieldLength, lengthAdjustment, lengthIncludesLengthFieldLength);
		this.lengthFieldFormat = lengthFieldFormat;
        this.lengthFieldLength = lengthFieldLength;
        this.lengthIncludesLengthFieldLength = lengthIncludesLengthFieldLength;
        this.lengthAdjustment = lengthAdjustment;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		if (LengthFieldFormat.BIN == lengthFieldFormat) {
			super.encode(ctx, msg, out);
			return;
		}

		int length = msg.readableBytes() + lengthAdjustment;
        if (lengthIncludesLengthFieldLength) {
            length += lengthFieldLength;
        }

        if (length < 0) {
            throw new IllegalArgumentException(
                    "Adjusted frame length (" + length + ") is less than zero");
        }

		out.add(ctx.alloc().buffer(lengthFieldLength).writeBytes(encodeLength(length)));
        out.add(msg.retain());
	}

	private byte[] encodeLength(int length) {
		if (LengthFieldFormat.BCD == lengthFieldFormat)
			return encodeLengthBCD(length);

		if (LengthFieldFormat.DEC == lengthFieldFormat)
			return encodeLengthDEC(length);

		if (LengthFieldFormat.HEX == lengthFieldFormat)
			return encodeLengthHEX(length);

		throw new DecoderException(
        		"unsupported lengthFieldFormat: "
        		+ lengthFieldFormat
        		+ " (expected: BCD, BIN, DEC, HEX)");		
	}

	private byte[] encodeLengthBCD(int length) {
		if (length >= Math.pow(10, lengthFieldLength * 2)) {
            throw new IllegalArgumentException(
                    "length does not fit into " + lengthFieldLength + " digits: " + length);
		}
		String lengthFormat = String.format("%%0%dd", lengthFieldLength * 2);
		String lengthString = String.format(lengthFormat, length);
		byte[] lengthBytes = new byte[lengthFieldLength];
		for (int i = 0; i < lengthFieldLength; i++) {
			lengthBytes[i] = (byte) (((byte)lengthString.charAt(i * 2) << 4) | (byte)(lengthString.charAt(i * 2 + 1) & 0x0F));
		}
		return lengthBytes;
	}

	private byte[] encodeLengthDEC(int length) {
		if (length >= Math.pow(10, lengthFieldLength)) {
            throw new IllegalArgumentException(
                    "length does not fit into " + lengthFieldLength + " digits: " + length);
		}
		String lengthFormat = String.format("%%0%dd", lengthFieldLength);
		String lengthString = String.format(lengthFormat, length);
		return lengthString.getBytes();
	}

	private byte[] encodeLengthHEX(int length) {
		if (length >= Math.pow(16, lengthFieldLength)) {
            throw new IllegalArgumentException(
                    "length does not fit into " + lengthFieldLength + " xdigits: " + length);
		}
		String lengthFormat = String.format("%%0%dX", lengthFieldLength);
		String lengthString = String.format(lengthFormat, length);
		return lengthString.getBytes();
	}

}
