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
import com.velocitypowered.proxy.VelocityServer;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import io.netty.buffer.ByteBuf;
import org.apache.logging.log4j.LogManager;

import java.util.Arrays;

public class Team implements MinecraftPacket {

    private String name;
    /**
     * 0 - create, 1 remove, 2 info update, 3 player add, 4 player remove.
     */
    private byte mode;
    private String displayName;
    private String prefix;
    private String suffix;
    private String nameTagVisibility;
    private String collisionRule;
    private int color;
    private byte friendlyFire;
    private String[] players;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getMode() {
        return mode;
    }

    public void setMode(byte mode) {
        this.mode = mode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getNameTagVisibility() {
        return nameTagVisibility;
    }

    public void setNameTagVisibility(String nameTagVisibility) {
        this.nameTagVisibility = nameTagVisibility;
    }

    public byte getFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(byte friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getCollisionRule() {
        return collisionRule;
    }

    public void setCollisionRule(String collisionRule) {
        this.collisionRule = collisionRule;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String[] getPlayers() {
        return players;
    }

    public void setPlayers(String[] players) {
        this.players = players;
    }

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        name = ProtocolUtils.readString(buf);
        mode = buf.readByte();
        if (mode == 0 || mode == 2) {
            displayName = ProtocolUtils.readString(buf);
            if (protocolVersion.getProtocol() < ProtocolVersion.MINECRAFT_1_13.getProtocol()) {
                prefix = ProtocolUtils.readString(buf);
                suffix = ProtocolUtils.readString(buf);
            }
            friendlyFire = buf.readByte();
            nameTagVisibility = ProtocolUtils.readString(buf);
            if (protocolVersion.getProtocol() >= ProtocolVersion.MINECRAFT_1_9.getProtocol()) {
                collisionRule = ProtocolUtils.readString(buf);
            }
            color = (protocolVersion.getProtocol() >= ProtocolVersion.MINECRAFT_1_13.getProtocol()) ? ProtocolUtils.readVarInt(buf) : buf.readByte();
            if (protocolVersion.getProtocol() >= ProtocolVersion.MINECRAFT_1_13.getProtocol()) {
                prefix = ProtocolUtils.readString(buf);
                suffix = ProtocolUtils.readString(buf);
            }
        }
        if (mode == 0 || mode == 3 || mode == 4) {
            int len = ProtocolUtils.readVarInt(buf);
            players = new String[len];
            for (int i = 0; i < len; i++) {
                players[i] = ProtocolUtils.readString(buf);
            }
        }
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        try{
            ProtocolUtils.writeString(buf, name);
            buf.writeByte(mode);
            if (mode == 0 || mode == 2) {
                ProtocolUtils.writeString(buf, displayName);
                if (protocolVersion.getProtocol() < ProtocolVersion.MINECRAFT_1_13.getProtocol()) {
                    ProtocolUtils.writeString(buf, prefix);
                    ProtocolUtils.writeString(buf, suffix);
                }
                buf.writeByte(friendlyFire);
                ProtocolUtils.writeString(buf, nameTagVisibility);
                if (protocolVersion.getProtocol() >= ProtocolVersion.MINECRAFT_1_9.getProtocol()) {
                    ProtocolUtils.writeString(buf, collisionRule);
                }

                if (protocolVersion.getProtocol() >= ProtocolVersion.MINECRAFT_1_13.getProtocol()) {
                    ProtocolUtils.writeVarInt(buf, color);
                    ProtocolUtils.writeString(buf, prefix);
                    ProtocolUtils.writeString(buf, suffix);
                } else {
                    buf.writeByte(color);
                }
            }
            if (mode == 0 || mode == 3 || mode == 4) {
                ProtocolUtils.writeVarInt(buf, players.length);
                for (String player : players) {
                    ProtocolUtils.writeString(buf, player);
                }
            }
        }catch(Throwable thr) {
            thr.printStackTrace();
            LogManager.getLogger(VelocityServer.class).info("There was an error!");
        }
    }

    @Override
    public boolean handle(MinecraftSessionHandler handler) {
        return handler.handle(this);
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                ", mode=" + mode +
                ", displayName='" + displayName + '\'' +
                ", prefix='" + prefix + '\'' +
                ", suffix='" + suffix + '\'' +
                ", nameTagVisibility='" + nameTagVisibility + '\'' +
                ", collisionRule='" + collisionRule + '\'' +
                ", color=" + color +
                ", friendlyFire=" + friendlyFire +
                ", players=" + Arrays.toString(players) +
                '}';
    }
}
