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

public class ScoreboardDisplay implements MinecraftPacket
{

    /**
     * 0 = list, 1 = side, 2 = below.
     */
    private byte position;
    private String name;

    public byte getPosition() {
        return position;
    }

    public void setPosition(byte position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        position = buf.readByte();
        name = ProtocolUtils.readString(buf);
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        buf.writeByte(position);
        ProtocolUtils.writeString(buf, name);
    }

    @Override
    public boolean handle(MinecraftSessionHandler handler) {
        return handler.handle(this);
    }
}
