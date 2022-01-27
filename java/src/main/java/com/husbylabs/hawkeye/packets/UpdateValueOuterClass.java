// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: protos/update_value.proto

package com.husbylabs.hawkeye.packets;

public final class UpdateValueOuterClass {
    private UpdateValueOuterClass() {
    }

    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistry registry) {
        registerAllExtensions(
                (com.google.protobuf.ExtensionRegistryLite) registry);
    }

    public interface UpdateValueOrBuilder extends
            // @@protoc_insertion_point(interface_extends:UpdateValue)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <code>string name = 1;</code>
         *
         * @return The name.
         */
        java.lang.String getName();

        /**
         * <code>string name = 1;</code>
         *
         * @return The bytes for name.
         */
        com.google.protobuf.ByteString
        getNameBytes();

        /**
         * <code>int32 id = 2;</code>
         *
         * @return The id.
         */
        int getId();
    }

    /**
     * Protobuf type {@code UpdateValue}
     */
    public static final class UpdateValue extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:UpdateValue)
            UpdateValueOrBuilder {
        private static final long serialVersionUID = 0L;

        // Use UpdateValue.newBuilder() to construct.
        private UpdateValue(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private UpdateValue() {
            name_ = "";
        }

        @java.lang.Override
        @SuppressWarnings({ "unused" })
        protected java.lang.Object newInstance(
                UnusedPrivateParameter unused) {
            return new UpdateValue();
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return this.unknownFields;
        }

        private UpdateValue(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new java.lang.NullPointerException();
            }
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
                        case 10: {
                            java.lang.String s = input.readStringRequireUtf8();

                            name_ = s;
                            break;
                        }
                        case 16: {

                            id_ = input.readInt32();
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
                this.unknownFields = unknownFields.build();
                makeExtensionsImmutable();
            }
        }

        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return com.husbylabs.hawkeye.packets.UpdateValueOuterClass.internal_static_UpdateValue_descriptor;
        }

        @java.lang.Override
        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return com.husbylabs.hawkeye.packets.UpdateValueOuterClass.internal_static_UpdateValue_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue.class, com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue.Builder.class);
        }

        public static final int NAME_FIELD_NUMBER = 1;
        private volatile java.lang.Object name_;

        /**
         * <code>string name = 1;</code>
         *
         * @return The name.
         */
        @java.lang.Override
        public java.lang.String getName() {
            java.lang.Object ref = name_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                name_ = s;
                return s;
            }
        }

        /**
         * <code>string name = 1;</code>
         *
         * @return The bytes for name.
         */
        @java.lang.Override
        public com.google.protobuf.ByteString
        getNameBytes() {
            java.lang.Object ref = name_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                name_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int ID_FIELD_NUMBER = 2;
        private int id_;

        /**
         * <code>int32 id = 2;</code>
         *
         * @return The id.
         */
        @java.lang.Override
        public int getId() {
            return id_;
        }

        private byte memoizedIsInitialized = -1;

        @java.lang.Override
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }

            memoizedIsInitialized = 1;
            return true;
        }

        @java.lang.Override
        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(name_)) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 1, name_);
            }
            if (id_ != 0) {
                output.writeInt32(2, id_);
            }
            unknownFields.writeTo(output);
        }

        @java.lang.Override
        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) {
                return size;
            }

            size = 0;
            if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(name_)) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, name_);
            }
            if (id_ != 0) {
                size += com.google.protobuf.CodedOutputStream
                        .computeInt32Size(2, id_);
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
            if (!(obj instanceof com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue)) {
                return super.equals(obj);
            }
            com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue other = (com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue) obj;

            if (!getName()
                    .equals(other.getName())) {
                return false;
            }
            if (getId()
                != other.getId()) {
                return false;
            }
            if (!unknownFields.equals(other.unknownFields)) {
                return false;
            }
            return true;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + NAME_FIELD_NUMBER;
            hash = (53 * hash) + getName().hashCode();
            hash = (37 * hash) + ID_FIELD_NUMBER;
            hash = (53 * hash) + getId();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }

        public static com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }

        public static com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }

        public static com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        @java.lang.Override
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue prototype) {
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
         * Protobuf type {@code UpdateValue}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:UpdateValue)
                com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValueOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return com.husbylabs.hawkeye.packets.UpdateValueOuterClass.internal_static_UpdateValue_descriptor;
            }

            @java.lang.Override
            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return com.husbylabs.hawkeye.packets.UpdateValueOuterClass.internal_static_UpdateValue_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue.class, com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue.Builder.class);
            }

            // Construct using com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue.newBuilder()
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
                name_ = "";

                id_ = 0;

                return this;
            }

            @java.lang.Override
            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return com.husbylabs.hawkeye.packets.UpdateValueOuterClass.internal_static_UpdateValue_descriptor;
            }

            @java.lang.Override
            public com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue getDefaultInstanceForType() {
                return com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue.getDefaultInstance();
            }

            @java.lang.Override
            public com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue build() {
                com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            @java.lang.Override
            public com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue buildPartial() {
                com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue result = new com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue(this);
                result.name_ = name_;
                result.id_ = id_;
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
                if (other instanceof com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue) {
                    return mergeFrom((com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue) other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue other) {
                if (other == com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue.getDefaultInstance()) {
                    return this;
                }
                if (!other.getName().isEmpty()) {
                    name_ = other.name_;
                    onChanged();
                }
                if (other.getId() != 0) {
                    setId(other.getId());
                }
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
                com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private java.lang.Object name_ = "";

            /**
             * <code>string name = 1;</code>
             *
             * @return The name.
             */
            public java.lang.String getName() {
                java.lang.Object ref = name_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    name_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }

            /**
             * <code>string name = 1;</code>
             *
             * @return The bytes for name.
             */
            public com.google.protobuf.ByteString
            getNameBytes() {
                java.lang.Object ref = name_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    name_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }

            /**
             * <code>string name = 1;</code>
             *
             * @param value The name to set.
             * @return This builder for chaining.
             */
            public Builder setName(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                name_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>string name = 1;</code>
             *
             * @return This builder for chaining.
             */
            public Builder clearName() {

                name_ = getDefaultInstance().getName();
                onChanged();
                return this;
            }

            /**
             * <code>string name = 1;</code>
             *
             * @param value The bytes for name to set.
             * @return This builder for chaining.
             */
            public Builder setNameBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                name_ = value;
                onChanged();
                return this;
            }

            private int id_;

            /**
             * <code>int32 id = 2;</code>
             *
             * @return The id.
             */
            @java.lang.Override
            public int getId() {
                return id_;
            }

            /**
             * <code>int32 id = 2;</code>
             *
             * @param value The id to set.
             * @return This builder for chaining.
             */
            public Builder setId(int value) {

                id_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>int32 id = 2;</code>
             *
             * @return This builder for chaining.
             */
            public Builder clearId() {

                id_ = 0;
                onChanged();
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


            // @@protoc_insertion_point(builder_scope:UpdateValue)
        }

        // @@protoc_insertion_point(class_scope:UpdateValue)
        private static final com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue DEFAULT_INSTANCE;

        static {
            DEFAULT_INSTANCE = new com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue();
        }

        public static com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<UpdateValue>
                PARSER = new com.google.protobuf.AbstractParser<UpdateValue>() {
            @java.lang.Override
            public UpdateValue parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new UpdateValue(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<UpdateValue> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<UpdateValue> getParserForType() {
            return PARSER;
        }

        @java.lang.Override
        public com.husbylabs.hawkeye.packets.UpdateValueOuterClass.UpdateValue getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_UpdateValue_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_UpdateValue_fieldAccessorTable;

    public static com.google.protobuf.Descriptors.FileDescriptor
    getDescriptor() {
        return descriptor;
    }

    private static com.google.protobuf.Descriptors.FileDescriptor
            descriptor;

    static {
        java.lang.String[] descriptorData = {
                "\n\031protos/update_value.proto\"\'\n\013UpdateVal" +
                "ue\022\014\n\004name\030\001 \001(\t\022\n\n\002id\030\002 \001(\005B\037\n\035com.husb" +
                "ylabs.hawkeye.packetsb\006proto3"
        };
        descriptor = com.google.protobuf.Descriptors.FileDescriptor
                .internalBuildGeneratedFileFrom(descriptorData,
                        new com.google.protobuf.Descriptors.FileDescriptor[]{
                        });
        internal_static_UpdateValue_descriptor =
                getDescriptor().getMessageTypes().get(0);
        internal_static_UpdateValue_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_UpdateValue_descriptor,
                new java.lang.String[]{ "Name", "Id", });
    }

    // @@protoc_insertion_point(outer_class_scope)
}
