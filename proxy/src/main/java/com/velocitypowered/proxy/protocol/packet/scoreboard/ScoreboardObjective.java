/*
 * Copyright (C) 2018 Velocity Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.velocitypowered.proxy.protocol.packet.scoreboard;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import io.netty.buffer.ByteBuf;

import java.util.Locale;

public class ScoreboardObjective implements MinecraftPacket {
    private String name;
    private String value;
    private HealthDisplay type;
    /**
     * 0 to create, 1 to remove, 2 to update display text.
     */
    private byte action;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public HealthDisplay getType() {
        return type;
    }

    public void setType(HealthDisplay type) {
        this.type = type;
    }

    public byte getAction() {
        return action;
    }

    public void setAction(byte action) {
        this.action = action;
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        ProtocolUtils.writeString(buf, name);
        buf.writeByte(action);
        if (action == 0 || action == 2) {
            ProtocolUtils.writeString(buf, value);
            if (protocolVersion.getProtocol() >= ProtocolVersion.MINECRAFT_1_13.getProtocol()) {
                ProtocolUtils.writeVarInt(buf, type.ordinal());
            } else {
                ProtocolUtils.writeString(buf, type.toString());
            }
        }
    }

    @Override
    public boolean handle(MinecraftSessionHandler handler) {
        return handler.handle(this);
    }

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        name = ProtocolUtils.readString(buf);
        action = buf.readByte();
        if (action == 0 || action == 2) {
            value = ProtocolUtils.readString(buf);
            if (protocolVersion.getProtocol() >= ProtocolVersion.MINECRAFT_1_13.getProtocol()) {
                type = HealthDisplay.values()[ProtocolUtils.readVarInt(buf)];
            } else {
                type = HealthDisplay.fromString(ProtocolUtils.readString(buf));
            }
        }
    }

    public enum HealthDisplay {

        INTEGER, HEARTS;

        @Override
        public String toString() {
            return super.toString().toLowerCase(Locale.ROOT);
        }

        public static HealthDisplay fromString(String s) {
            return valueOf(s.toUpperCase(Locale.ROOT));
        }
    }
}