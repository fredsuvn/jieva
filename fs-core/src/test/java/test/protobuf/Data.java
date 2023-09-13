// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: message.proto

package test.protobuf;

/**
 * Protobuf type {@code Data}
 */
public final class Data extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:Data)
    DataOrBuilder {
private static final long serialVersionUID = 0L;
  // Use Data.newBuilder() to construct.
  private Data(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private Data() {
    em_ = 0;
    str_ = "";
    text_ = com.google.protobuf.LazyStringArrayList.EMPTY;
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new Data();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private Data(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 8: {
            int rawValue = input.readEnum();

            em_ = rawValue;
            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();

            str_ = s;
            break;
          }
          case 24: {

            num_ = input.readInt64();
            break;
          }
          case 34: {
            java.lang.String s = input.readStringRequireUtf8();
            if (!((mutable_bitField0_ & 0x00000001) != 0)) {
              text_ = new com.google.protobuf.LazyStringArrayList();
              mutable_bitField0_ |= 0x00000001;
            }
            text_.add(s);
            break;
          }
          case 42: {
            if (!((mutable_bitField0_ & 0x00000002) != 0)) {
              entry_ = com.google.protobuf.MapField.newMapField(
                  EntryDefaultEntryHolder.defaultEntry);
              mutable_bitField0_ |= 0x00000002;
            }
            com.google.protobuf.MapEntry<java.lang.String, java.lang.String>
            entry__ = input.readMessage(
                EntryDefaultEntryHolder.defaultEntry.getParserForType(), extensionRegistry);
            entry_.getMutableMap().put(
                entry__.getKey(), entry__.getValue());
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      if (((mutable_bitField0_ & 0x00000001) != 0)) {
        text_ = text_.getUnmodifiableView();
      }
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return test.protobuf.Message.internal_static_Data_descriptor;
  }

  @SuppressWarnings({"rawtypes"})
  @java.lang.Override
  protected com.google.protobuf.MapField internalGetMapField(
      int number) {
    switch (number) {
      case 5:
        return internalGetEntry();
      default:
        throw new RuntimeException(
            "Invalid map field number: " + number);
    }
  }
  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return test.protobuf.Message.internal_static_Data_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            test.protobuf.Data.class, test.protobuf.Data.Builder.class);
  }

  public static final int EM_FIELD_NUMBER = 1;
  private int em_;
  /**
   * <code>.Enum em = 1;</code>
   * @return The enum numeric value on the wire for em.
   */
  @java.lang.Override public int getEmValue() {
    return em_;
  }
  /**
   * <code>.Enum em = 1;</code>
   * @return The em.
   */
  @java.lang.Override public test.protobuf.Enum getEm() {
    @SuppressWarnings("deprecation")
    test.protobuf.Enum result = test.protobuf.Enum.valueOf(em_);
    return result == null ? test.protobuf.Enum.UNRECOGNIZED : result;
  }

  public static final int STR_FIELD_NUMBER = 2;
  private volatile java.lang.Object str_;
  /**
   * <code>string str = 2;</code>
   * @return The str.
   */
  @java.lang.Override
  public java.lang.String getStr() {
    java.lang.Object ref = str_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs =
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      str_ = s;
      return s;
    }
  }
  /**
   * <code>string str = 2;</code>
   * @return The bytes for str.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getStrBytes() {
    java.lang.Object ref = str_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b =
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      str_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int NUM_FIELD_NUMBER = 3;
  private long num_;
  /**
   * <code>int64 num = 3;</code>
   * @return The num.
   */
  @java.lang.Override
  public long getNum() {
    return num_;
  }

  public static final int TEXT_FIELD_NUMBER = 4;
  private com.google.protobuf.LazyStringList text_;
  /**
   * <code>repeated string text = 4;</code>
   * @return A list containing the text.
   */
  public com.google.protobuf.ProtocolStringList
      getTextList() {
    return text_;
  }
  /**
   * <code>repeated string text = 4;</code>
   * @return The count of text.
   */
  public int getTextCount() {
    return text_.size();
  }
  /**
   * <code>repeated string text = 4;</code>
   * @param index The index of the element to return.
   * @return The text at the given index.
   */
  public java.lang.String getText(int index) {
    return text_.get(index);
  }
  /**
   * <code>repeated string text = 4;</code>
   * @param index The index of the value to return.
   * @return The bytes of the text at the given index.
   */
  public com.google.protobuf.ByteString
      getTextBytes(int index) {
    return text_.getByteString(index);
  }

  public static final int ENTRY_FIELD_NUMBER = 5;
  private static final class EntryDefaultEntryHolder {
    static final com.google.protobuf.MapEntry<
        java.lang.String, java.lang.String> defaultEntry =
            com.google.protobuf.MapEntry
            .<java.lang.String, java.lang.String>newDefaultInstance(
                test.protobuf.Message.internal_static_Data_EntryEntry_descriptor,
                com.google.protobuf.WireFormat.FieldType.STRING,
                "",
                com.google.protobuf.WireFormat.FieldType.STRING,
                "");
  }
  private com.google.protobuf.MapField<
      java.lang.String, java.lang.String> entry_;
  private com.google.protobuf.MapField<java.lang.String, java.lang.String>
  internalGetEntry() {
    if (entry_ == null) {
      return com.google.protobuf.MapField.emptyMapField(
          EntryDefaultEntryHolder.defaultEntry);
    }
    return entry_;
  }

  public int getEntryCount() {
    return internalGetEntry().getMap().size();
  }
  /**
   * <code>map&lt;string, string&gt; entry = 5;</code>
   */

  @java.lang.Override
  public boolean containsEntry(
      java.lang.String key) {
    if (key == null) { throw new java.lang.NullPointerException(); }
    return internalGetEntry().getMap().containsKey(key);
  }
  /**
   * Use {@link #getEntryMap()} instead.
   */
  @java.lang.Override
  @java.lang.Deprecated
  public java.util.Map<java.lang.String, java.lang.String> getEntry() {
    return getEntryMap();
  }
  /**
   * <code>map&lt;string, string&gt; entry = 5;</code>
   */
  @java.lang.Override

  public java.util.Map<java.lang.String, java.lang.String> getEntryMap() {
    return internalGetEntry().getMap();
  }
  /**
   * <code>map&lt;string, string&gt; entry = 5;</code>
   */
  @java.lang.Override

  public java.lang.String getEntryOrDefault(
      java.lang.String key,
      java.lang.String defaultValue) {
    if (key == null) { throw new java.lang.NullPointerException(); }
    java.util.Map<java.lang.String, java.lang.String> map =
        internalGetEntry().getMap();
    return map.containsKey(key) ? map.get(key) : defaultValue;
  }
  /**
   * <code>map&lt;string, string&gt; entry = 5;</code>
   */
  @java.lang.Override

  public java.lang.String getEntryOrThrow(
      java.lang.String key) {
    if (key == null) { throw new java.lang.NullPointerException(); }
    java.util.Map<java.lang.String, java.lang.String> map =
        internalGetEntry().getMap();
    if (!map.containsKey(key)) {
      throw new java.lang.IllegalArgumentException();
    }
    return map.get(key);
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (em_ != test.protobuf.Enum.E1.getNumber()) {
      output.writeEnum(1, em_);
    }
    if (!getStrBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, str_);
    }
    if (num_ != 0L) {
      output.writeInt64(3, num_);
    }
    for (int i = 0; i < text_.size(); i++) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 4, text_.getRaw(i));
    }
    com.google.protobuf.GeneratedMessageV3
      .serializeStringMapTo(
        output,
        internalGetEntry(),
        EntryDefaultEntryHolder.defaultEntry,
        5);
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (em_ != test.protobuf.Enum.E1.getNumber()) {
      size += com.google.protobuf.CodedOutputStream
        .computeEnumSize(1, em_);
    }
    if (!getStrBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, str_);
    }
    if (num_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(3, num_);
    }
    {
      int dataSize = 0;
      for (int i = 0; i < text_.size(); i++) {
        dataSize += computeStringSizeNoTag(text_.getRaw(i));
      }
      size += dataSize;
      size += 1 * getTextList().size();
    }
    for (java.util.Map.Entry<java.lang.String, java.lang.String> entry
         : internalGetEntry().getMap().entrySet()) {
      com.google.protobuf.MapEntry<java.lang.String, java.lang.String>
      entry__ = EntryDefaultEntryHolder.defaultEntry.newBuilderForType()
          .setKey(entry.getKey())
          .setValue(entry.getValue())
          .build();
      size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(5, entry__);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof test.protobuf.Data)) {
      return super.equals(obj);
    }
    test.protobuf.Data other = (test.protobuf.Data) obj;

    if (em_ != other.em_) return false;
    if (!getStr()
        .equals(other.getStr())) return false;
    if (getNum()
        != other.getNum()) return false;
    if (!getTextList()
        .equals(other.getTextList())) return false;
    if (!internalGetEntry().equals(
        other.internalGetEntry())) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + EM_FIELD_NUMBER;
    hash = (53 * hash) + em_;
    hash = (37 * hash) + STR_FIELD_NUMBER;
    hash = (53 * hash) + getStr().hashCode();
    hash = (37 * hash) + NUM_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getNum());
    if (getTextCount() > 0) {
      hash = (37 * hash) + TEXT_FIELD_NUMBER;
      hash = (53 * hash) + getTextList().hashCode();
    }
    if (!internalGetEntry().getMap().isEmpty()) {
      hash = (37 * hash) + ENTRY_FIELD_NUMBER;
      hash = (53 * hash) + internalGetEntry().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static test.protobuf.Data parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static test.protobuf.Data parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static test.protobuf.Data parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static test.protobuf.Data parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static test.protobuf.Data parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static test.protobuf.Data parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static test.protobuf.Data parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static test.protobuf.Data parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static test.protobuf.Data parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static test.protobuf.Data parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static test.protobuf.Data parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static test.protobuf.Data parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(test.protobuf.Data prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code Data}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:Data)
      test.protobuf.DataOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return test.protobuf.Message.internal_static_Data_descriptor;
    }

    @SuppressWarnings({"rawtypes"})
    protected com.google.protobuf.MapField internalGetMapField(
        int number) {
      switch (number) {
        case 5:
          return internalGetEntry();
        default:
          throw new RuntimeException(
              "Invalid map field number: " + number);
      }
    }
    @SuppressWarnings({"rawtypes"})
    protected com.google.protobuf.MapField internalGetMutableMapField(
        int number) {
      switch (number) {
        case 5:
          return internalGetMutableEntry();
        default:
          throw new RuntimeException(
              "Invalid map field number: " + number);
      }
    }
    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return test.protobuf.Message.internal_static_Data_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              test.protobuf.Data.class, test.protobuf.Data.Builder.class);
    }

    // Construct using test.proto.Data.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      em_ = 0;

      str_ = "";

      num_ = 0L;

      text_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000001);
      internalGetMutableEntry().clear();
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return test.protobuf.Message.internal_static_Data_descriptor;
    }

    @java.lang.Override
    public test.protobuf.Data getDefaultInstanceForType() {
      return test.protobuf.Data.getDefaultInstance();
    }

    @java.lang.Override
    public test.protobuf.Data build() {
      test.protobuf.Data result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public test.protobuf.Data buildPartial() {
      test.protobuf.Data result = new test.protobuf.Data(this);
      int from_bitField0_ = bitField0_;
      result.em_ = em_;
      result.str_ = str_;
      result.num_ = num_;
      if (((bitField0_ & 0x00000001) != 0)) {
        text_ = text_.getUnmodifiableView();
        bitField0_ = (bitField0_ & ~0x00000001);
      }
      result.text_ = text_;
      result.entry_ = internalGetEntry();
      result.entry_.makeImmutable();
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof test.protobuf.Data) {
        return mergeFrom((test.protobuf.Data)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(test.protobuf.Data other) {
      if (other == test.protobuf.Data.getDefaultInstance()) return this;
      if (other.em_ != 0) {
        setEmValue(other.getEmValue());
      }
      if (!other.getStr().isEmpty()) {
        str_ = other.str_;
        onChanged();
      }
      if (other.getNum() != 0L) {
        setNum(other.getNum());
      }
      if (!other.text_.isEmpty()) {
        if (text_.isEmpty()) {
          text_ = other.text_;
          bitField0_ = (bitField0_ & ~0x00000001);
        } else {
          ensureTextIsMutable();
          text_.addAll(other.text_);
        }
        onChanged();
      }
      internalGetMutableEntry().mergeFrom(
          other.internalGetEntry());
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      test.protobuf.Data parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (test.protobuf.Data) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private int em_ = 0;
    /**
     * <code>.Enum em = 1;</code>
     * @return The enum numeric value on the wire for em.
     */
    @java.lang.Override public int getEmValue() {
      return em_;
    }
    /**
     * <code>.Enum em = 1;</code>
     * @param value The enum numeric value on the wire for em to set.
     * @return This builder for chaining.
     */
    public Builder setEmValue(int value) {

      em_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>.Enum em = 1;</code>
     * @return The em.
     */
    @java.lang.Override
    public test.protobuf.Enum getEm() {
      @SuppressWarnings("deprecation")
      test.protobuf.Enum result = test.protobuf.Enum.valueOf(em_);
      return result == null ? test.protobuf.Enum.UNRECOGNIZED : result;
    }
    /**
     * <code>.Enum em = 1;</code>
     * @param value The em to set.
     * @return This builder for chaining.
     */
    public Builder setEm(test.protobuf.Enum value) {
      if (value == null) {
        throw new NullPointerException();
      }

      em_ = value.getNumber();
      onChanged();
      return this;
    }
    /**
     * <code>.Enum em = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearEm() {

      em_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object str_ = "";
    /**
     * <code>string str = 2;</code>
     * @return The str.
     */
    public java.lang.String getStr() {
      java.lang.Object ref = str_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        str_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string str = 2;</code>
     * @return The bytes for str.
     */
    public com.google.protobuf.ByteString
        getStrBytes() {
      java.lang.Object ref = str_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        str_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string str = 2;</code>
     * @param value The str to set.
     * @return This builder for chaining.
     */
    public Builder setStr(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }

      str_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string str = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearStr() {

      str_ = getDefaultInstance().getStr();
      onChanged();
      return this;
    }
    /**
     * <code>string str = 2;</code>
     * @param value The bytes for str to set.
     * @return This builder for chaining.
     */
    public Builder setStrBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

      str_ = value;
      onChanged();
      return this;
    }

    private long num_ ;
    /**
     * <code>int64 num = 3;</code>
     * @return The num.
     */
    @java.lang.Override
    public long getNum() {
      return num_;
    }
    /**
     * <code>int64 num = 3;</code>
     * @param value The num to set.
     * @return This builder for chaining.
     */
    public Builder setNum(long value) {

      num_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int64 num = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearNum() {

      num_ = 0L;
      onChanged();
      return this;
    }

    private com.google.protobuf.LazyStringList text_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    private void ensureTextIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        text_ = new com.google.protobuf.LazyStringArrayList(text_);
        bitField0_ |= 0x00000001;
       }
    }
    /**
     * <code>repeated string text = 4;</code>
     * @return A list containing the text.
     */
    public com.google.protobuf.ProtocolStringList
        getTextList() {
      return text_.getUnmodifiableView();
    }
    /**
     * <code>repeated string text = 4;</code>
     * @return The count of text.
     */
    public int getTextCount() {
      return text_.size();
    }
    /**
     * <code>repeated string text = 4;</code>
     * @param index The index of the element to return.
     * @return The text at the given index.
     */
    public java.lang.String getText(int index) {
      return text_.get(index);
    }
    /**
     * <code>repeated string text = 4;</code>
     * @param index The index of the value to return.
     * @return The bytes of the text at the given index.
     */
    public com.google.protobuf.ByteString
        getTextBytes(int index) {
      return text_.getByteString(index);
    }
    /**
     * <code>repeated string text = 4;</code>
     * @param index The index to set the value at.
     * @param value The text to set.
     * @return This builder for chaining.
     */
    public Builder setText(
        int index, java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureTextIsMutable();
      text_.set(index, value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string text = 4;</code>
     * @param value The text to add.
     * @return This builder for chaining.
     */
    public Builder addText(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureTextIsMutable();
      text_.add(value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string text = 4;</code>
     * @param values The text to add.
     * @return This builder for chaining.
     */
    public Builder addAllText(
        java.lang.Iterable<java.lang.String> values) {
      ensureTextIsMutable();
      com.google.protobuf.AbstractMessageLite.Builder.addAll(
          values, text_);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string text = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearText() {
      text_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000001);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string text = 4;</code>
     * @param value The bytes of the text to add.
     * @return This builder for chaining.
     */
    public Builder addTextBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      ensureTextIsMutable();
      text_.add(value);
      onChanged();
      return this;
    }

    private com.google.protobuf.MapField<
        java.lang.String, java.lang.String> entry_;
    private com.google.protobuf.MapField<java.lang.String, java.lang.String>
    internalGetEntry() {
      if (entry_ == null) {
        return com.google.protobuf.MapField.emptyMapField(
            EntryDefaultEntryHolder.defaultEntry);
      }
      return entry_;
    }
    private com.google.protobuf.MapField<java.lang.String, java.lang.String>
    internalGetMutableEntry() {
      onChanged();;
      if (entry_ == null) {
        entry_ = com.google.protobuf.MapField.newMapField(
            EntryDefaultEntryHolder.defaultEntry);
      }
      if (!entry_.isMutable()) {
        entry_ = entry_.copy();
      }
      return entry_;
    }

    public int getEntryCount() {
      return internalGetEntry().getMap().size();
    }
    /**
     * <code>map&lt;string, string&gt; entry = 5;</code>
     */

    @java.lang.Override
    public boolean containsEntry(
        java.lang.String key) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      return internalGetEntry().getMap().containsKey(key);
    }
    /**
     * Use {@link #getEntryMap()} instead.
     */
    @java.lang.Override
    @java.lang.Deprecated
    public java.util.Map<java.lang.String, java.lang.String> getEntry() {
      return getEntryMap();
    }
    /**
     * <code>map&lt;string, string&gt; entry = 5;</code>
     */
    @java.lang.Override

    public java.util.Map<java.lang.String, java.lang.String> getEntryMap() {
      return internalGetEntry().getMap();
    }
    /**
     * <code>map&lt;string, string&gt; entry = 5;</code>
     */
    @java.lang.Override

    public java.lang.String getEntryOrDefault(
        java.lang.String key,
        java.lang.String defaultValue) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      java.util.Map<java.lang.String, java.lang.String> map =
          internalGetEntry().getMap();
      return map.containsKey(key) ? map.get(key) : defaultValue;
    }
    /**
     * <code>map&lt;string, string&gt; entry = 5;</code>
     */
    @java.lang.Override

    public java.lang.String getEntryOrThrow(
        java.lang.String key) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      java.util.Map<java.lang.String, java.lang.String> map =
          internalGetEntry().getMap();
      if (!map.containsKey(key)) {
        throw new java.lang.IllegalArgumentException();
      }
      return map.get(key);
    }

    public Builder clearEntry() {
      internalGetMutableEntry().getMutableMap()
          .clear();
      return this;
    }
    /**
     * <code>map&lt;string, string&gt; entry = 5;</code>
     */

    public Builder removeEntry(
        java.lang.String key) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      internalGetMutableEntry().getMutableMap()
          .remove(key);
      return this;
    }
    /**
     * Use alternate mutation accessors instead.
     */
    @java.lang.Deprecated
    public java.util.Map<java.lang.String, java.lang.String>
    getMutableEntry() {
      return internalGetMutableEntry().getMutableMap();
    }
    /**
     * <code>map&lt;string, string&gt; entry = 5;</code>
     */
    public Builder putEntry(
        java.lang.String key,
        java.lang.String value) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      if (value == null) { throw new java.lang.NullPointerException(); }
      internalGetMutableEntry().getMutableMap()
          .put(key, value);
      return this;
    }
    /**
     * <code>map&lt;string, string&gt; entry = 5;</code>
     */

    public Builder putAllEntry(
        java.util.Map<java.lang.String, java.lang.String> values) {
      internalGetMutableEntry().getMutableMap()
          .putAll(values);
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:Data)
  }

  // @@protoc_insertion_point(class_scope:Data)
  private static final test.protobuf.Data DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new test.protobuf.Data();
  }

  public static test.protobuf.Data getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<Data>
      PARSER = new com.google.protobuf.AbstractParser<Data>() {
    @java.lang.Override
    public Data parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new Data(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<Data> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<Data> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public test.protobuf.Data getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

