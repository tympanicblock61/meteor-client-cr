/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.network;

import com.github.puzzle.core.registries.GenericRegistry;
import com.github.puzzle.core.registries.IRegistry;
import finalforeach.cosmicreach.networking.GamePacket;
import finalforeach.cosmicreach.networking.packets.*;
import finalforeach.cosmicreach.networking.packets.blocks.*;
import finalforeach.cosmicreach.networking.packets.entities.*;
import finalforeach.cosmicreach.networking.packets.items.ContainerSyncPacket;
import finalforeach.cosmicreach.networking.packets.items.DropItemPacket;
import finalforeach.cosmicreach.networking.packets.items.RequestGiveItemPacket;
import finalforeach.cosmicreach.networking.packets.items.SlotSyncPacket;
import finalforeach.cosmicreach.networking.packets.meta.*;
import finalforeach.cosmicreach.networking.packets.sounds.*;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import meteordevelopment.meteorclient.MeteorClient;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public class PacketUtils {
    public static final IRegistry<Class<? extends GamePacket>> REGISTRY = new PacketRegistry();

    private static final Map<Class<? extends GamePacket>, String> S2C_PACKETS = new Reference2ObjectOpenHashMap<>();
    private static final Map<Class<? extends GamePacket>, String> C2S_PACKETS = new Reference2ObjectOpenHashMap<>();

    private static final Map<String, Class<? extends GamePacket>> S2C_PACKETS_R = new Object2ReferenceOpenHashMap<>();
    private static final Map<String, Class<? extends GamePacket>> C2S_PACKETS_R = new Object2ReferenceOpenHashMap<>();

    static {
        C2S_PACKETS.put(ProtocolSyncPacket.class, "ProtocolSyncPacket");
        C2S_PACKETS_R.put("ProtocolSyncPacket", ProtocolSyncPacket.class);
        C2S_PACKETS.put(TransactionPacket.class, "TransactionPacket");
        C2S_PACKETS_R.put("TransactionPacket", TransactionPacket.class);
        C2S_PACKETS.put(LoginPacket.class, "LoginPacket");
        C2S_PACKETS_R.put("LoginPacket", LoginPacket.class);
        C2S_PACKETS.put(WorldRecievedGamePacket.class, "WorldRecievedGamePacket");
        C2S_PACKETS_R.put("WorldRecievedGamePacket", WorldRecievedGamePacket.class);
        C2S_PACKETS.put(SetNetworkSetting.class, "SetNetworkSetting");
        C2S_PACKETS_R.put("SetNetworkSetting", SetNetworkSetting.class);
        C2S_PACKETS.put(ItchSessionTokenPacket.class, "ItchSessionTokenPacket");
        C2S_PACKETS_R.put("ItchSessionTokenPacket", ItchSessionTokenPacket.class);
        C2S_PACKETS.put(MessagePacket.class, "MessagePacket");
        C2S_PACKETS_R.put("MessagePacket", MessagePacket.class);
        C2S_PACKETS.put(PlayerPositionPacket.class, "PlayerPositionPacket");
        C2S_PACKETS_R.put("PlayerPositionPacket", PlayerPositionPacket.class);
        C2S_PACKETS.put(NoClipPacket.class, "NoClipPacket");
        C2S_PACKETS_R.put("NoClipPacket", NoClipPacket.class);
        C2S_PACKETS.put(CommandPacket.class, "CommandPacket");
        C2S_PACKETS_R.put("CommandPacket", CommandPacket.class);
        C2S_PACKETS.put(PlaceBlockPacket.class, "PlaceBlockPacket");
        C2S_PACKETS_R.put("PlaceBlockPacket", PlaceBlockPacket.class);
        C2S_PACKETS.put(BreakBlockPacket.class, "BreakBlockPacket");
        C2S_PACKETS_R.put("BreakBlockPacket", BreakBlockPacket.class);
        C2S_PACKETS.put(InteractBlockPacket.class, "InteractBlockPacket");
        C2S_PACKETS_R.put("InteractBlockPacket", InteractBlockPacket.class);
        C2S_PACKETS.put(DropItemPacket.class, "DropItemPacket");
        C2S_PACKETS_R.put("DropItemPacket", DropItemPacket.class);
        // SlotInteractPacket not added 100% yet so skip it
        C2S_PACKETS.put(BlockEntityContainerSyncPacket.class, "BlockEntityContainerSyncPacket");
        C2S_PACKETS_R.put("BlockEntityContainerSyncPacket", BlockEntityContainerSyncPacket.class);
        C2S_PACKETS.put(DropItemFromBlockEntityContainerPacket.class, "DropItemFromBlockEntityContainerPacket");
        C2S_PACKETS_R.put("DropItemFromBlockEntityContainerPacket", DropItemFromBlockEntityContainerPacket.class);
        C2S_PACKETS.put(SignsEntityPacket.class, "SignsEntityPacket");
        C2S_PACKETS_R.put("SignsEntityPacket", SignsEntityPacket.class);
        C2S_PACKETS.put(RequestGiveItemPacket.class, "RequestGiveItemPacket");
        C2S_PACKETS_R.put("RequestGiveItemPacket", RequestGiveItemPacket.class);
        C2S_PACKETS.put(AttackEntityPacket.class, "AttackEntityPacket");
        C2S_PACKETS_R.put("AttackEntityPacket", AttackEntityPacket.class);
        C2S_PACKETS.put(InteractEntityPacket.class, "InteractEntityPacket");
        C2S_PACKETS_R.put("InteractEntityPacket", InteractEntityPacket.class);
        C2S_PACKETS.put(RespawnPacket.class, "RespawnPacket");
        C2S_PACKETS_R.put("RespawnPacket", RespawnPacket.class);

        S2C_PACKETS.put(ProtocolSyncPacket.class, "ProtocolSyncPacket");
        S2C_PACKETS_R.put("ProtocolSyncPacket", ProtocolSyncPacket.class);
        S2C_PACKETS.put(TransactionPacket.class, "TransactionPacket");
        S2C_PACKETS_R.put("TransactionPacket", TransactionPacket.class);
        S2C_PACKETS.put(RemovedPlayerPacket.class, "RemovedPlayerPacket");
        S2C_PACKETS_R.put("RemovedPlayerPacket", RemovedPlayerPacket.class);
        S2C_PACKETS.put(EndTickPacket.class, "EndTickPacket");
        S2C_PACKETS_R.put("EndTickPacket", EndTickPacket.class);
        S2C_PACKETS.put(SetNetworkSetting.class, "SetNetworkSetting");
        S2C_PACKETS_R.put("SetNetworkSetting", SetNetworkSetting.class);
        S2C_PACKETS.put(ChallengeLoginPacket.class, "ChallengeLoginPacket");
        S2C_PACKETS_R.put("ChallengeLoginPacket", ChallengeLoginPacket.class);
        S2C_PACKETS.put(PlayerPacket.class, "PlayerPacket");
        S2C_PACKETS_R.put("PlayerPacket", PlayerPacket.class);
        S2C_PACKETS.put(MessagePacket.class, "MessagePacket");
        S2C_PACKETS_R.put("MessagePacket", MessagePacket.class);
        S2C_PACKETS.put(PlayerPositionPacket.class, "PlayerPositionPacket");
        S2C_PACKETS_R.put("PlayerPositionPacket", PlayerPositionPacket.class);
        S2C_PACKETS.put(EntityPositionPacket.class, "EntityPositionPacket");
        S2C_PACKETS_R.put("EntityPositionPacket", EntityPositionPacket.class);
        S2C_PACKETS.put(NoClipPacket.class, "NoClipPacket");
        S2C_PACKETS_R.put("NoClipPacket", NoClipPacket.class);
        S2C_PACKETS.put(ZonePacket.class, "ZonePacket");
        S2C_PACKETS_R.put("ZonePacket", ZonePacket.class);
        S2C_PACKETS.put(ChunkColumnPacket.class, "ChunkColumnPacket");
        S2C_PACKETS_R.put("ChunkColumnPacket", ChunkColumnPacket.class);
        S2C_PACKETS.put(DisconnectPacket.class, "DisconnectPacket");
        S2C_PACKETS_R.put("DisconnectPacket", DisconnectPacket.class);
        S2C_PACKETS.put(BlockReplacePacket.class, "BlockReplacePacket");
        S2C_PACKETS_R.put("BlockReplacePacket", BlockReplacePacket.class);
        S2C_PACKETS.put(PlaySound2DPacket.class, "PlaySound2DPacket");
        S2C_PACKETS_R.put("PlaySound2DPacket", PlaySound2DPacket.class);
        S2C_PACKETS.put(PlaySound3DPacket.class, "PlaySound3DPacket");
        S2C_PACKETS_R.put("PlaySound3DPacket", PlaySound3DPacket.class);
        S2C_PACKETS.put(ContainerSyncPacket.class, "ContainerSyncPacket");
        S2C_PACKETS_R.put("ContainerSyncPacket", ContainerSyncPacket.class);
        S2C_PACKETS.put(BlockEntityContainerSyncPacket.class, "BlockEntityContainerSyncPacket");
        S2C_PACKETS_R.put("BlockEntityContainerSyncPacket", BlockEntityContainerSyncPacket.class);
        S2C_PACKETS.put(BlockEntityScreenPacket.class, "BlockEntityScreenPacket");
        S2C_PACKETS_R.put("BlockEntityScreenPacket", BlockEntityScreenPacket.class);
        S2C_PACKETS.put(BlockEntityDataPacket.class, "BlockEntityDataPacket");
        S2C_PACKETS_R.put("BlockEntityDataPacket", BlockEntityDataPacket.class);
        S2C_PACKETS.put(SlotSyncPacket.class, "SlotSyncPacket");
        S2C_PACKETS_R.put("SlotSyncPacket", SlotSyncPacket.class);
        S2C_PACKETS.put(SpawnEntityPacket.class, "SpawnEntityPacket");
        S2C_PACKETS_R.put("SpawnEntityPacket", SpawnEntityPacket.class);
        S2C_PACKETS.put(DespawnEntityPacket.class, "DespawnEntityPacket");
        S2C_PACKETS_R.put("DespawnEntityPacket", DespawnEntityPacket.class);
        S2C_PACKETS.put(HitEntityPacket.class, "HitEntityPacket");
        S2C_PACKETS_R.put("HitEntityPacket", HitEntityPacket.class);
        S2C_PACKETS.put(MaxHPEntityPacket.class, "MaxHPEntityPacket");
        S2C_PACKETS_R.put("MaxHPEntityPacket", MaxHPEntityPacket.class);
        S2C_PACKETS.put(ParticleSystemPacket.class, "ParticleSystemPacket");
        S2C_PACKETS_R.put("ParticleSystemPacket", ParticleSystemPacket.class);
        S2C_PACKETS.put(SetMusicTagsPacket.class, "SetMusicTagsPacket");
        S2C_PACKETS_R.put("SetMusicTagsPacket", SetMusicTagsPacket.class);
        S2C_PACKETS.put(ForceSongChangePacket.class, "ForceSongChangePacket");
        S2C_PACKETS_R.put("ForceSongChangePacket", ForceSongChangePacket.class);
    }

    private PacketUtils() {
    }

    public static String getName(Class<? extends GamePacket> packetClass) {
        String name = S2C_PACKETS.get(packetClass);
        if (name != null) return name;
        return C2S_PACKETS.get(packetClass);
    }

    public static Class<? extends GamePacket> getPacket(String name) {
        Class<? extends GamePacket> packet = S2C_PACKETS_R.get(name);
        if (packet != null) return packet;
        return C2S_PACKETS_R.get(name);
    }

    public static Set<Class<? extends GamePacket>> getS2CPackets() {
        return S2C_PACKETS.keySet();
    }

    public static Set<Class<? extends GamePacket>> getC2SPackets() {
        return C2S_PACKETS.keySet();
    }

    private static class PacketRegistry extends GenericRegistry<Class<? extends GamePacket>> {
        public PacketRegistry() {
            super(MeteorClient.identifier("packets"));
        }

        public int size() {
            return S2C_PACKETS.keySet().size() + C2S_PACKETS.keySet().size();
        }

        @NotNull
        @Override
        public Iterator<Class<? extends GamePacket>> iterator() {
            return Stream.concat(S2C_PACKETS.keySet().stream(), C2S_PACKETS.keySet().stream()).iterator();
        }

        @Override
        public void freeze() {
            return;
        }
    }
}
