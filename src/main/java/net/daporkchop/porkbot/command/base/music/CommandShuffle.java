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

package net.daporkchop.porkbot.command.base.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.daporkchop.porkbot.PorkBot;
import net.daporkchop.porkbot.command.Command;
import net.daporkchop.porkbot.music.GuildAudioInfo;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CommandShuffle extends Command {
    public CommandShuffle() {
        super("shuffle");
    }

    public void execute(MessageReceivedEvent evt, String[] split, String rawContent) {
        evt.getTextChannel().sendMessage("Shuffling queue...").queue(message -> {
            GuildAudioInfo info = PorkBot.INSTANCE.getGuildAudioPlayer(evt.getGuild(), false);
            if (info == null)   {
                message.editMessage("Not playing!").queue();
            } else {
                BlockingQueue<AudioTrack> queue = info.manager.scheduler.queue;
                AudioTrack[] tracks = queue.toArray(new AudioTrack[queue.size()]);
                ArrayList<Integer> usedIndexes = new ArrayList<>();
                while (usedIndexes.size() < tracks.length) {
                    int index = PorkBot.random.nextInt(tracks.length);
                    if (!usedIndexes.contains(index)) {
                        AudioTrack track = tracks[index];
                        tracks[index] = track;
                        usedIndexes.add(index);
                    }
                }
                BlockingQueue<AudioTrack> newQueue = new LinkedBlockingQueue<>();
                for (AudioTrack track : tracks) {
                    newQueue.add(track);
                }
                info.manager.scheduler.queue = newQueue;
                message.editMessage("Queue shuffled!").queue();
            }
        });
    }
}
