import gemini

p = gemini.PublicClient()
r = gemini.PrivateClient("bXVM9YaofU9ply00F9aO", "352ks5RLMzntpfJYgJceBge71cgq", sandbox=True)

lists = []

lists = p.symbols()

for list in lists:

    print (p.get_ticker(list))
    print(list)
