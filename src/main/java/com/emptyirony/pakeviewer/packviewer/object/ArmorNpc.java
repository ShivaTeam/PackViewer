package com.emptyirony.pakeviewer.packviewer.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.jitse.npclib.api.NPC;
import team.shiva.core.util.Cooldown;

/**
 * 2 * @Author: EmptyIrony
 * 3 * @Date: 2020/1/17 17:50
 * 4
 */
@Data
@AllArgsConstructor
public class ArmorNpc {
    private NPC npc;
    private Armor armor;
    private Cooldown cooldown;
}
