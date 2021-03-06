/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2016 DaPorkchop_
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it.
 * Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.porkbot;

import net.daporkchop.porkbot.command.CommandRegistry;
import net.daporkchop.porkbot.util.ShardUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;

public class PorkListener extends ListenerAdapter {
    public JDA shard;

    public PorkListener(JDA jda) {
        shard = jda;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            //bots don't matter to us!
            return;
        }
        String message = event.getMessage().getRawContent();

        if (message.startsWith("..")) {
            CommandRegistry.runCommand(event, message, shard);
        } else if (event.getChannelType().ordinal() == ChannelType.PRIVATE.ordinal()) {
            if (event.getAuthor().getId().equals("226975061880471552")) {
                switch (message) {
                    case ",,instareboot":
                        event.getChannel().sendMessage("Rebooting...").queue();
                        System.out.println("Rebooting...");
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(500);
                                    CommandRegistry.save();
                                    ShardUtils.shutdown();
                                    System.exit(0);
                                } catch (InterruptedException e) {

                                }
                            }
                        }.start();
                        return;
                }

                if (message.startsWith(",,announce ")) {
                    String toAnnouce = message.substring(11);

                    List<Guild> servers = ShardUtils.guilds;
                    for (Guild server : servers) {
                        try {
                            server.getDefaultChannel().sendMessage(toAnnouce).queue();
                        } catch (PermissionException e) {
                            //who cares, we can't do anything about it
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        ShardUtils.guilds.add(event.getGuild());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        ShardUtils.guilds.remove(event.getGuild());
    }
}
