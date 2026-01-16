self.addEventListener("fetch", event => {
  if (event.request.url.includes("pokeapi.co/api") ||  event.request.url.includes("/api/pokemon")) {
    event.respondWith(
      caches.open("pokeapi-cache").then(cache =>
        cache.match(event.request).then(resp =>
          resp || fetch(event.request).then(net => {
            cache.put(event.request, net.clone());
            return net;
          })
        )
      )
    );
  }
});
