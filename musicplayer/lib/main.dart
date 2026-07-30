import 'package:flutter/material.dart';
import 'package:just_audio/just_audio.dart';
import 'package:just_audio_background/just_audio_background.dart';
import 'package:on_audio_query/on_audio_query.dart';
import 'package:permission_handler/permission_handler.dart';

Future<void> main() async {
  // Инициализация фона (уведомления)
  await JustAudioBackground.init(
    androidNotificationChannelId: 'com.my.music.channel.audio',
    androidNotificationChannelName: 'Music Playback',
    androidNotificationOngoing: true,
  );
  runApp(const MaterialApp(home: MusicApp(), debugShowCheckedModeBanner: false));
}

class MusicApp extends StatefulWidget {
  const MusicApp({super.key});
  @override
  State<MusicApp> createState() => _MusicAppState();
}

class _MusicAppState extends State<MusicApp> {
  final OnAudioQuery _audioQuery = OnAudioQuery();
  final AudioPlayer _player = AudioPlayer();

  @override
  void initState() {
    super.initState();
    requestPermissions();
  }

  void requestPermissions() async {
    await Permission.storage.request();
    await Permission.audio.request();
    setState(() {});
  }

  void playSong(SongModel song) {
    _player.setAudioSource(
      AudioSource.uri(
        Uri.parse(song.uri!),
        tag: MediaItem(
          id: '${song.id}',
          album: song.album,
          title: song.title,
          artist: song.artist,
          artUri: Uri.parse("https://api.dicebear.com/7.x/identicon/png?seed=${song.title}"),
        ),
      ),
    );
    _player.play();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF090909),
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            colors: [Color(0xFF1A1A2E), Color(0xFF090909)],
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
          ),
        ),
        child: SafeArea(
          child: Column(
            children: [
              const Padding(
                padding: EdgeInsets.all(20),
                child: Text("Cloud Player", style: TextStyle(color: Colors.white, fontSize: 26, fontWeight: FontWeight.bold)),
              ),
              Expanded(
                child: FutureBuilder<List<SongModel>>(
                  future: _audioQuery.querySongs(sortType: null, orderType: OrderType.ASC_OR_SMALLER, uriType: UriType.EXTERNAL, ignoreCase: true),
                  builder: (context, item) {
                    if (item.data == null) return const Center(child: CircularProgressIndicator());
                    if (item.data!.isEmpty) return const Center(child: Text("Треки не найдены", style: TextStyle(color: Colors.white)));
                    return ListView.builder(
                      itemCount: item.data!.length,
                      itemBuilder: (context, index) {
                        var song = item.data![index];
                        return ListTile(
                          title: Text(song.title, style: const TextStyle(color: Colors.white), maxLines: 1),
                          subtitle: Text(song.artist ?? "Unknown", style: const TextStyle(color: Colors.white54)),
                          leading: QueryArtworkWidget(id: song.id, type: ArtworkType.AUDIO, nullArtworkWidget: const Icon(Icons.music_note, color: Colors.blueAccent)),
                          onTap: () => playSong(song),
                        );
                      },
                    );
                  },
                ),
              ),
              // Мини-плеер
              Container(
                padding: const EdgeInsets.all(10),
                color: Colors.black26,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    IconButton(onPressed: () {}, icon: const Icon(Icons.skip_previous, color: Colors.white)),
                    StreamBuilder<bool>(
                      stream: _player.playingStream,
                      builder: (context, snapshot) {
                        bool playing = snapshot.data ?? false;
                        return IconButton(
                          iconSize: 50,
                          onPressed: () => playing ? _player.pause() : _player.play(),
                          icon: Icon(playing ? Icons.pause_circle : Icons.play_circle, color: Colors.blueAccent),
                        );
                      },
                    ),
                    IconButton(onPressed: () {}, icon: const Icon(Icons.skip_next, color: Colors.white)),
                  ],
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
}