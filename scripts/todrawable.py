import sys, os

def main():
   
    fname = sys.argv[1]
    layout =  sys.argv[2]
    directory = "../res/drawable/" + layout
    f = open(fname, "r")
    lines = f.readlines()

    if not os.path.exists(directory):
        os.makedirs(directory)
    xml = ""
    color = ""
    for line in lines:
        if "----------" in line:
            out = open(directory + "_color_" + color +".xml", "w")
            out.write(xml)
            xml = ""
            color = ""
            continue

        if "android:color=" in line:
            color = line.split("""android:color="#""")[1]
            color = color.split("""" />""")[0]
            color = color[2:]
            print color
        xml += line


if __name__ == '__main__':
    main()