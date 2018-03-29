import gemini

p = gemini.PublicClient()
r = gemini.PrivateClient("bXVM9YaofU9ply00F9aO", "352ks5RLMzntpfJYgJceBge71cgq", sandbox=True)

lists = []

lists = p.symbols()

for list in lists:

    print (p.get_ticker(list))
    print(list)
import math, urllib2, json, re


def download():
    graph = {}
    page = urllib2.urlopen("https://bittrex.com/api/v1.1/public/getmarketsummaries")
    data = page.read()
    jsrates = json.loads(data)

    result_list = jsrates["result"]
    for result_index, result in enumerate(result_list):
        ask = result["Ask"]
        bid = result["Bid"]
        market = result["MarketName"]
        pattern = re.compile("([A-Z0-9]*)-([A-Z0-9]*)")
        matches = pattern.match(market)
        if matches:
            from_rate = matches.group(1).encode('ascii', 'ignore')
            to_rate = matches.group(2).encode('ascii', 'ignore')

            # different sign of log is effectively 1/x
            if ask != 0:
                if from_rate not in graph:
                    graph[from_rate] = {}
                graph[from_rate][to_rate] = math.log(float(ask))
            if bid != 0:
                if to_rate not in graph:
                    graph[to_rate] = {}
                graph[to_rate][from_rate] = -math.log(float(bid))

    return graph  # Step 1: For each node prepare the destination and predecessor


def initialize(graph, source):
    d = {}  # Stands for destination
    p = {}  # Stands for predecessor
    for node in graph:
        d[node] = float('Inf')  # We start admiting that the rest of nodes are very very far
        p[node] = None
    d[source] = 0  # For the source we know how to reach
    return d, p


def relax(node, neighbour, graph, d, p):
    # If the distance between the node and the neighbour is lower than the one I have now
    dist = graph[node][neighbour]
    if d[neighbour] > d[node] + dist:
        # Record this lower distance
        d[neighbour] = d[node] + dist
        p[neighbour] = node


def retrace_negative_loop(p, start):
    arbitrageLoop = [start]
    prev_node = start
    while True:
        prev_node = p[prev_node]
        if prev_node not in arbitrageLoop:
            arbitrageLoop.append(prev_node)
        else:
            arbitrageLoop.append(prev_node)
            arbitrageLoop = arbitrageLoop[arbitrageLoop.index(prev_node):]
            # return arbitrageLoop
            return list(reversed(arbitrageLoop))


def bellman_ford(graph, source):
    d, p = initialize(graph, source)
    for i in range(len(graph) - 1):  # Run this until is converges
        for u in graph:
            for v in graph[u]:  # For each neighbour of u
                relax(u, v, graph, d, p)  # Lets relax it

    # Step 3: check for negative-weight cycles
    for u in graph:
        for v in graph[u]:
            if d[v] < d[u] + graph[u][v]:
                return retrace_negative_loop(p, v)
    return None




graph = download()

# print graph
for k, v in graph.iteritems():
    print "{0} => {1}".format(k, v)
print "-------------------------------"

paths = []
for currency in graph:
    path = bellman_ford(graph, currency)
    if path not in paths and not None:
        paths.append(path)

for path in paths:
    if path == None:
        print("No opportunity here :(")
    else:
        money = 100
        print "Starting with %(money)i in %(currency)s" % {"money": money, "currency": path[0]}

        for i, value in enumerate(path):
            if i + 1 < len(path):
                start = path[i]
                end = path[i + 1]
                rate = math.exp(-graph[start][end])
                money *= rate
                print "%(start)s to %(end)s at %(rate)f = %(money)f" % {"start": start, "end": end, "rate": rate,
                                                                        "money": money}

    print "\n"
