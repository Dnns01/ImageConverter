package propra.imageconverter.imageheader;

import propra.imageconverter.Checksum;
import propra.imageconverter.Compression;
import propra.imageconverter.PixelOrder;
import propra.imageconverter.exceptions.InvalidImageException;
import propra.imageconverter.handler.ByteHandler;

import java.nio.ByteBuffer;

/**
 * Image header in ProPra Format.
 */
public class ProPraImageHeader extends ImageHeader {

    public static final int HEADER_SIZE = 28;
    public static PixelOrder PIXEL_ORDER = PixelOrder.GBR;

    /**
     * Magic String a ProPra image starts with.
     */
    private String magic;

    /**
     * Data segment size field from image file header.
     */
    private long dataSegmentSize;

    /**
     * Checksum field from image file header.
     */
    private int checksum;

    /**
     * Constructs a ProPra image with xOrigin, yOrigin, width, height, pixelDepth, image descriptor and pixel data of
     * image.
     *
     * @param magic           magic String the image starts with. Has to be "ProPraWS19".
     * @param imgWidth        width of image.
     * @param imgHeight       height of image.
     * @param pixelDepth      pixel depth of image.
     * @param compression     compression type field from image file header.
     * @param dataSegmentSize size of data segment.
     * @param checksum        checksum of pixel data.
     * @throws InvalidImageException if image header is invalid.
     */
    public ProPraImageHeader(String magic, short imgWidth, short imgHeight, byte pixelDepth, Compression compression,
                             long dataSegmentSize, int checksum) throws InvalidImageException {
        super(imgWidth, imgHeight, pixelDepth, compression);

        this.magic = magic;
        this.dataSegmentSize = dataSegmentSize;
        this.checksum = checksum;

        if (!magic.equals("ProPraWS19")) {
            throw new InvalidImageException("Loaded ProPra Image does not start with String \"ProPraWS19\"");
        }
    }

    /**
     * Returns magic field from image file header.
     *
     * @return magic.
     */
    public String getMagic() {
        return magic;
    }

    /**
     * Returns data segment size field from image file header.
     *
     * @return dataSegmentSize.
     */
    public long getDataSegmentSize() {
        return dataSegmentSize;
    }

    /**
     * Returns checksum field from image file header.
     *
     * @return checksum.
     */
    public int getChecksum() {
        return checksum;
    }

    /**
     * Returns header as byte array to be written into a file.
     *
     * @return this header as byte array.
     */
    @Override
    public byte[] toByteArray() {
        ByteBuffer buf = ByteBuffer.allocate(HEADER_SIZE);

        buf.put(this.getMagic().getBytes());
        buf.put(ByteHandler.shortToByteArray(this.getImgWidth()));
        buf.put(ByteHandler.shortToByteArray(this.getImgHeight()));
        buf.put(this.getPixelDepth());
        buf.put((byte) (this.getCompression() == Compression.Uncompressed ? 0 : 1));
        buf.put(ByteHandler.longToByteArray(this.getDataSegmentSize()));
        buf.put(ByteHandler.intToByteArray(this.getChecksum()));

        return buf.array();
    }

    @Override
    public PixelOrder getPixelOrder() {
        return PIXEL_ORDER;
    }

    /**
     * Calculates checksum from image data.
     *
     * @param checksum calculated checksum, that should get verified against the checksum in header.
     * @throws InvalidImageException if the calculated checksum mismachts the checksum from header.
     */
    public void validateChecksum(Checksum checksum) throws InvalidImageException {
        if (this.getChecksum() != checksum.getChecksum()) {
            throw new InvalidImageException(String.format("Mismatch between read checksum(0x%08X) and " +
                    "calculated checksum(%s). Please verify.", this.getChecksum(), checksum));
        }
    }
}

