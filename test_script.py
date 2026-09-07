import xml.etree.ElementTree as ET
import glob
for f in glob.glob('/app/build/test-results/test/TEST-thaumcraft.common.golems.GolemTraitsTest.xml'):
    tree = ET.parse(f)
    root = tree.getroot()
    for testcase in root.findall('testcase'):
        failure = testcase.find('failure')
        if failure is not None:
            print("FAILED:", testcase.get('name'))
            print(failure.text)
